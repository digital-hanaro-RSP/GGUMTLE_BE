package com.hana4.ggumtle.dto.bucketlist;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BucketResponseDto {
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class BucketInfo {
        private String title;
        private BucketTagType tagType;
        private LocalDateTime dueDate;
        private BucketHowTo howTo;
        private Boolean isDueSet;
        private Boolean isAutoAllocate;
        private BigDecimal allocateAmount;
        private String cronCycle;
        private BigDecimal goalAmount;
        private String memo;
        private BucketStatus status;
        private Boolean isRecommended;
        private BigDecimal originId;


        public static BucketInfo form(Bucket bucket) {
            return BucketInfo.builder()
                    .title(bucket.getTitle())
                    .tagType(bucket.getTagType())
                    .dueDate(bucket.getDueDate())
                    .howTo(bucket.getHowTo())
                    .isDueSet(bucket.getIsDueSet())
                    .isAutoAllocate(bucket.getIsAutoAllocate())
                    .allocateAmount(bucket.getAllocateAmount())
                    .cronCycle(bucket.getCronCycle())
                    .goalAmount(bucket.getGoalAmount())
                    .memo(bucket.getMemo())
                    .status(bucket.getStatus())
                    .isRecommended(bucket.getIsRecommended())
                    .originId(bucket.getOriginId())
                    .build();
        }
    }
}
