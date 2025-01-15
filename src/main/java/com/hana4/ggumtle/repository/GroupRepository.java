package com.hana4.ggumtle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hana4.ggumtle.model.entity.group.Group;
import com.hana4.ggumtle.model.entity.group.GroupCategory;

public interface GroupRepository extends JpaRepository<Group, Long> {
	@Query(
		"SELECT g, COUNT(gm) " +
			"FROM Group g LEFT JOIN GroupMember gm ON gm.group.id = g.id " +
			"WHERE (:category IS NULL OR g.category = :category) " +
			"AND (:search IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
			"GROUP BY g.id, g.name, g.category, g.description, g.imageUrl"
	)
	Page<Object[]> findGroupsWithMemberCount(
		@Param("category") GroupCategory category,
		@Param("search") String search,
		Pageable pageable
	);
}
