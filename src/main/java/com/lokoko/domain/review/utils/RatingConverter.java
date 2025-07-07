package com.lokoko.domain.review.utils;

import com.lokoko.domain.review.entity.enums.Rating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Rating rating) {
        if (rating == null) {
            return null;
        }
        return rating.getValue();
    }

    @Override
    public Rating convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return Rating.fromValue(dbData);
    }
}
