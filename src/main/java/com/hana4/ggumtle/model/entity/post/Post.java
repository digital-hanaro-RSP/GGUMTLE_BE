package com.hana4.ggumtle.model.entity.post;

import com.hana4.ggumtle.model.entity.BaseEntity;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_Post_userId_User"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "groupId", nullable = false, foreignKey = @ForeignKey(name = "fk_Post_groupId_Group"))
    private Group group;

    @Column(columnDefinition = "json")
    private String snapshot;

    @Column(columnDefinition = "json")
    private String imageUrls;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;
}
