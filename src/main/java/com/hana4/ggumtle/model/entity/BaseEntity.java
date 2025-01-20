package com.hana4.ggumtle.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseEntity {
	@CreationTimestamp
	@Column(nullable = false, updatable = false, columnDefinition = "timestamp")
	@ColumnDefault("CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false, columnDefinition = "timestamp")
	@ColumnDefault("CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;
}
