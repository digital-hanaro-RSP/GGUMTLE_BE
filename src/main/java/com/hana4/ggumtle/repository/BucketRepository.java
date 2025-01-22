package com.hana4.ggumtle.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

public interface BucketRepository extends JpaRepository<Bucket, Long> {
	Optional<Bucket> findById(Long bucketId);

	List<Bucket> findAll();

	@Query("SELECT b FROM Bucket b WHERE b.dreamAccount.id = :dreamAccountId")
	List<Bucket> findAllByDreamAccountId(@Param("dreamAccountId") Long dreamAccountId);

	// DreamAccount ID로 관련된 Bucket의 safeBox 합계 계산
	@Query("SELECT COALESCE(SUM(b.safeBox), 0) FROM Bucket b WHERE b.dreamAccount.id = :dreamAccountId")
	BigDecimal getTotalSafeBoxByDreamAccountId(@Param("dreamAccountId") Long dreamAccountId);

	List<Bucket> findByIsRecommendedTrue();

	// tagType에 맞는 버킷 조회
	List<Bucket> findByTagType(BucketTagType tagType);
}
