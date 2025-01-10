package com.hana4.ggumtle.model.entity.bucket;

import com.hana4.ggumtle.model.entity.BaseEntity;
import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;
import com.hana4.ggumtle.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Bucket")
public class Bucket extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dreamAccountId", nullable = false, foreignKey = @ForeignKey(name = "fk_Bucket_dreamAccountId_DreamAccount"))
    private DreamAccount dreamAccount;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_Bucket_userId_User"))
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BucketTagType tagType;

    private LocalDateTime dueDate;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDueSet;

    private String memo;

    @Enumerated(EnumType.STRING)
    private BucketHowTo howTo;

    private BigDecimal goalAmount;

    private Long followers;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BucketStatus status;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isAutoAllocate;

    private BigDecimal allocateAmount;

    private String cronCycle;

    private BigDecimal safeBox;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isRecommended;
}
