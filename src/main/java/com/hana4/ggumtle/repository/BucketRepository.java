package com.hana4.ggumtle.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketHowTo;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

public interface BucketRepository extends JpaRepository<Bucket, Long> {
	Optional<Bucket> findById(Long bucketId);

	List<Bucket> findAllByUserId(String userId);

	List<Bucket> findByIsRecommendedTrue();

	// tagType에 맞는 버킷 조회
	List<Bucket> findByTagType(BucketTagType tagType);

	List<Bucket> findByTagTypeAndIsRecommendedTrue(BucketTagType tagType);

	@Query("SELECT b FROM Bucket b WHERE b.user.id = :userId AND b.howTo = :howTo AND (b.dueDate IS NULL OR b.dueDate > :dueDate) AND b.status = 'DOING'")
	List<Bucket> findValidBuckets(@Param("userId") String userId,
		@Param("howTo") BucketHowTo howTo,
		@Param("dueDate") LocalDateTime dueDate);
}
