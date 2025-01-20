package com.hana4.ggumtle.model.entity.survey;

import com.hana4.ggumtle.model.entity.BaseEntity;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
public class Survey extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_Survey_userId_User"))
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SurveyType surveyType;
}
