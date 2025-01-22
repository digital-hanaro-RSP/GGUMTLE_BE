package com.hana4.ggumtle.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hana4.ggumtle.model.entity.dreamAccount.DreamAccount;

@Repository
public interface DreamAccountRepository extends JpaRepository<DreamAccount, Long> {
	// JpaRepository에서 기본 제공하는 메소드로 DreamAccount 엔티티 관리
	Optional<DreamAccount> findByUserId(String userId);

}
