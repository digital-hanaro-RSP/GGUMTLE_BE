package com.hana4.ggumtle.dto.bucketlist;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketStatus;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;
import com.hana4.ggumtle.model.entity.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class BucketRequestDto {

    @NotNull
    private String title;

    @NotNull
    private BucketTagType tagType;

    @NotNull
    private LocalDateTime dueDate;

    @NotNull
    private BucketHowTo howTo;
    private Boolean isDueSet;

    private BigDecimal safeBox;
    private Boolean isAutoAllocate;

    private BigDecimal allocateAmount;

    @NotNull
    private String cronCycle;

    @NotNull
    private BigDecimal goalAmount;

    @NotNull
    private BucketStatus status;
    private String memo;

    // 추가 필드 (추천 플로우에서만 사용)
    private Boolean isRecommended; // 선택적 필드
    private BigDecimal originId;      // 선택적 필드

    public Bucket toEntity() {

        return new Bucket().toBuilder()
                .title(this.title)
                .tagType(this.tagType)
                .dueDate(this.dueDate)
                .howTo(this.howTo)
                .isDueSet(this.isDueSet)
                .isAutoAllocate(this.isAutoAllocate)
                .allocateAmount(this.allocateAmount)
                .cronCycle(this.cronCycle)
                .goalAmount(this.goalAmount)
                .memo(this.memo)
                .status(this.status)
                .isRecommended(this.isRecommended)
                .originId(this.originId)
                .build();
    }

}
