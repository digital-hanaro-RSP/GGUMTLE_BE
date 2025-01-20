package com.hana4.ggumtle.model.entity.commentLike;

import java.time.LocalDateTime;

import com.hana4.ggumtle.model.entity.comment.Comment;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
