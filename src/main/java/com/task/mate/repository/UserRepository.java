package com.task.mate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.task.mate.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	@Query("SELECT u FROM User u WHERE u.email = ?1")
	public User findByEmail(String email);

	@Query("UPDATE User u SET u.enabled =true WHERE u.id= ?1")
	@Modifying
	public void enable(Long id);

	@Query("SELECT u FROM User u WHERE u.verificationCode= ?1")
	public User findByVerificationCode(String Code);

	public User findByResetPasswordToken(String token);
}
