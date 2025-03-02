package com.Adil.RestAPI.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, updatable = false)
	private UUID userId = UUID.randomUUID();

	@ManyToOne
	@JoinColumn(name = "manager_id")
	private Manager manager;

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false, unique = true)
	private String mobNum;

	@Column(nullable = false, unique = true)
	private String panNum;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private boolean isActive = true;
}