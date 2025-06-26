//package com.lokoko.domain.product.scheduler;
//
//import com.lokoko.domain.product.entity.Product;
//import com.lokoko.domain.product.entity.enums.MainCategory;
//import com.lokoko.domain.product.entity.enums.SubCategory;
//import com.lokoko.domain.product.repository.ProductRepository;
//import com.lokoko.domain.product.service.CrawlingService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ProductCrawlScheduler {
//    private final CrawlingService crawlingService;
//    private final ProductRepository productRepository;
//
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void crawlAndSave() {
//        for (var mc : MainCategory.values()) {
//            for (var sc : SubCategory.values()) {
//                if (sc.getParent() == mc) {
//                    var products = crawlingService.crawlCategory(mc, sc, 5).stream()
//                            .map(Product::fromDTO)
//                            .toList();
//                    productRepository.saveAll(products);
//                    log.info("[CrawlScheduler] {} - {} 크롤링 후 {}건 저장", mc, sc, products.size());
//                }
//            }
//        }
//    }
//}

/*
 * TODO: 크롤링 자동화시 활성화 예정
 */
