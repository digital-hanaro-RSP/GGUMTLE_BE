package com.hana4.ggumtle.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.bucket.Bucket;

public interface BucketRepository extends JpaRepository<Bucket, Long> {
	Optional<Bucket> findById(Long bucketId);

	List<Bucket> findAll();

}
