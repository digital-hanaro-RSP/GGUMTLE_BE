package com.hana4.ggumtle.model.entity.commentLike;

import com.hana4.ggumtle.model.entity.comment.Comment;
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
@Table(name = "CommentLike")
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_CommentLike_userId_User"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "commentId", nullable = false, foreignKey = @ForeignKey(name = "fk_CommentLike_commentId_Comment"))
    private Comment comment;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
