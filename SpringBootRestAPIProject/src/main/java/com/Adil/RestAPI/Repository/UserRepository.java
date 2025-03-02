package com.Adil.RestAPI.Repository;

import com.Adil.RestAPI.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserIdAndIsActiveTrue(UUID userId);
    Optional<User> findByMobNumAndIsActiveTrue(String mobNum);
    List<User> findAllByManager_ManagerIdAndIsActiveTrue(UUID managerId);
    boolean existsByMobNumAndIsActiveTrue(String mobNum);
    boolean existsByPanNumAndIsActiveTrue(String panNum);

    @Modifying
    @Query("DELETE FROM User u WHERE u.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    List<User> findAllByIsActiveTrue();
}