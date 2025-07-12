package com.lokoko.domain.review.service;

import com.lokoko.domain.image.entity.ReceiptImage;
import com.lokoko.domain.image.entity.ReviewImage;
import com.lokoko.domain.image.repository.ReceiptImageRepository;
import com.lokoko.domain.image.repository.ReviewImageRepository;
import com.lokoko.domain.product.entity.ProductOption;
import com.lokoko.domain.product.exception.ProductOptionMismatchException;
import com.lokoko.domain.product.exception.ProductOptionNotFoundException;
import com.lokoko.domain.product.repository.ProductOptionRepository;
import com.lokoko.domain.review.dto.request.ReviewMediaRequest;
import com.lokoko.domain.review.dto.request.ReviewReceiptRequest;
import com.lokoko.domain.review.dto.request.ReviewRequest;
import com.lokoko.domain.review.dto.response.MainImageReview;
import com.lokoko.domain.review.dto.response.MainImageReviewResponse;
import com.lokoko.domain.review.dto.response.ReviewMediaResponse;
import com.lokoko.domain.review.dto.response.ReviewReceiptResponse;
import com.lokoko.domain.review.dto.response.ReviewResponse;
import com.lokoko.domain.review.entity.Review;
import com.lokoko.domain.review.entity.enums.Rating;
import com.lokoko.domain.review.exception.ErrorMessage;
import com.lokoko.domain.review.exception.InvalidMediaTypeException;
import com.lokoko.domain.review.exception.ReceiptImageCountingException;
import com.lokoko.domain.review.repository.ReviewRepository;
import com.lokoko.domain.review.utils.S3UrlParser;
import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.exception.UserNotFoundException;
import com.lokoko.domain.user.repository.UserRepository;
import com.lokoko.domain.video.entity.ReviewVideo;
import com.lokoko.domain.video.repository.ReviewVideoRepository;
import com.lokoko.global.common.dto.PresignedUrlResponse;
import com.lokoko.global.common.entity.MediaFile;
import com.lokoko.global.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static com.lokoko.global.utils.AllowedMediaType.ALLOWED_MEDIA_TYPES;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final S3Service s3Service;

    private final ReviewRepository reviewRepository;
    private final ReceiptImageRepository receiptImageRepository;
    private final UserRepository userRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ReviewVideoRepository reviewVideoRepository;

    public ReviewReceiptResponse createReceiptPresignedUrl(Long userId,
                                                           ReviewReceiptRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        String mediaType = request.mediaType();

        // "image/"로 시작하는지, 슬래시가 포함되어 있는지 검사
        if (!(mediaType.startsWith("image/")) || !mediaType.contains("/")) {
            throw new InvalidMediaTypeException(ErrorMessage.INVALID_MEDIA_TYPE_FORMAT);
        }

        //  허용된 mediaType인지 체크
        if (!ALLOWED_MEDIA_TYPES.contains(mediaType)) {
            throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
        }

        PresignedUrlResponse response = s3Service.generatePresignedUrl(mediaType);
        String presignedUrl = response.presignedUrl();
        return new ReviewReceiptResponse(List.of(presignedUrl));
    }

    public ReviewMediaResponse createMediaPresignedUrl(
            Long userId,
            ReviewMediaRequest request) {
        userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<String> mediaTypes = request.mediaType();

        boolean hasVideo = mediaTypes.stream().anyMatch(type -> type.startsWith("video/"));
        boolean hasImage = mediaTypes.stream().anyMatch(type -> type.startsWith("image/"));

        if (hasVideo && hasImage) {
            throw new InvalidMediaTypeException(ErrorMessage.MIXED_MEDIA_TYPE_NOT_ALLOWED);
        }

        // 개수 제한 검증
        if (hasVideo && mediaTypes.size() > 1) {
            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_VIDEO_FILES);
        }

        if (hasImage && mediaTypes.size() > 5) {
            throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_IMAGE_FILES);
        }

        // 허용되지 않은 형식이 있는지 검증
        for (String type : mediaTypes) {
            if (!ALLOWED_MEDIA_TYPES.contains(type)) {
                throw new InvalidMediaTypeException(ErrorMessage.UNSUPPORTED_MEDIA_TYPE);
            }
        }

        // presigned URL 발급
        List<String> urls = mediaTypes.stream()
                .map(s3Service::generatePresignedUrl)
                .map(PresignedUrlResponse::presignedUrl)
                .toList();

        return new ReviewMediaResponse(urls);

    }

    @Transactional
    public ReviewResponse createReview(
            Long productId,
            Long userId,
            ReviewRequest request
    ) {
        ProductOption option = productOptionRepository.findById(request.productOptionId())
                .orElseThrow(ProductOptionNotFoundException::new);

        if (!option.getProduct().getId().equals(productId)) {
            throw new ProductOptionMismatchException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<String> receiptUrls = request.receiptUrl();
        List<String> mediaUrls = request.mediaUrl();

        // 영수증 1장 초과 검증
        if (receiptUrls != null && receiptUrls.size() > 1) {
            throw new ReceiptImageCountingException(ErrorMessage.TOO_MANY_RECEIPT_IMAGES);
        }

        // 미디어 검증 (동영상 1개 이하, 이미지 5개 이하, 혼용 불가)
        if (mediaUrls != null && !mediaUrls.isEmpty()) {
            long videoCount = mediaUrls.stream().filter(url -> url.contains("/video/")).count();
            long imageCount = mediaUrls.stream().filter(url -> url.contains("/image/")).count();

            if (videoCount > 0 && imageCount > 0) {
                throw new InvalidMediaTypeException(ErrorMessage.MIXED_MEDIA_TYPE_NOT_ALLOWED);
            }
            if (videoCount > 1) {
                throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_VIDEO_FILES);
            }
            if (imageCount > 5) {
                throw new InvalidMediaTypeException(ErrorMessage.TOO_MANY_IMAGE_FILES);
            }
        }

        Review review = Review.builder()
                .author(user)
                .product(option.getProduct())
                .productOption(option)
                .productInfo(option.getProduct().getProductDetail())
                .rating(Rating.fromValue(request.rating()))
                .positiveContent(request.positiveComment())
                .negativeContent(request.negativeComment())
                .likeCount(0)
                .build();

        reviewRepository.save(review);

        // 영수증 이미지 저장
        if (receiptUrls != null) {
            int order = 0;
            for (String url : receiptUrls) {
                MediaFile mediaFile = S3UrlParser.parsePresignedUrl(url);
                ReceiptImage ri = ReceiptImage.builder()
                        .mediaFile(mediaFile)
                        .displayOrder(order++)
                        .review(review)
                        .build();
                receiptImageRepository.save(ri);
            }
        }

        // 일반 이미지/비디오 저장
        if (mediaUrls != null) {
            int order = 0;
            for (String url : mediaUrls) {
                MediaFile mediaFile = S3UrlParser.parsePresignedUrl(url);
                if (url.contains("/video/")) {
                    ReviewVideo rv = ReviewVideo.createReviewVideo(mediaFile, order++, review);
                    reviewVideoRepository.save(rv);
                } else {
                    ReviewImage ri = ReviewImage.createReviewImage(mediaFile, order++, review);
                    reviewImageRepository.save(ri);
                }
            }
        }

        return new ReviewResponse(review.getId());
    }

    public MainImageReviewResponse getMainImageReview() {
        List<ReviewImage> sorted = reviewImageRepository.findMainImageReviewSorted();

        List<MainImageReview> dtoList = IntStream.range(0, sorted.size())
                .mapToObj(i -> MainImageReview.from(sorted.get(i), i + 1))
                .toList();

        return new MainImageReviewResponse(dtoList);
    }
}