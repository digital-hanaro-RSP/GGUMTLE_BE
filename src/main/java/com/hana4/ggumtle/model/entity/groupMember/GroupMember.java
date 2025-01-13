package com.hana4.ggumtle.model.entity.groupMember;

import com.hana4.ggumtle.model.entity.BaseEntity;
import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uniq_GroupMember_composition", columnNames = {"groupId",
		"userId"}))
public class GroupMember extends BaseEntity {
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne
		@JoinColumn(name = "groupId", nullable = false, foreignKey = @ForeignKey(name = "fk_GroupMember_groupId_Group"))
		private Group group;

		@ManyToOne
		@JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_GroupMember_groupId_User"))
		private User user;

}
