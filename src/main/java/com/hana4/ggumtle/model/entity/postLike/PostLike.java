package com.hana4.ggumtle.model.entity.postLike;

import com.hana4.ggumtle.model.entity.post.Post;
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
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_PostLike_userId_User"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "postId", nullable = false, foreignKey = @ForeignKey(name = "fk_PostLike_postId_Post"))
    private Post post;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
