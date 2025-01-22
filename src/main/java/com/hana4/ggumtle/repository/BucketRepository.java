package com.hana4.ggumtle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.bucket.Bucket;
import com.hana4.ggumtle.model.entity.bucket.BucketTagType;

public interface BucketRepository extends JpaRepository<Bucket, Long> {
	Optional<Bucket> findById(Long bucketId);

	List<Bucket> findAll();

	List<Bucket> findByIsRecommendedTrue();

	// tagType에 맞는 버킷 조회
	List<Bucket> findByTagType(BucketTagType tagType);
}
