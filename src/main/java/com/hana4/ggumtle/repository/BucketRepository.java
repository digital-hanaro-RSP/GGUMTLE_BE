package com.hana4.ggumtle.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hana4.ggumtle.model.entity.bucket.Bucket;

public interface BucketRepository extends JpaRepository<Bucket, Long> {
}
