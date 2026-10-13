package com.green.spring_board.repository;

import com.green.spring_board.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    void deleteByUserId(int userId);
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUserId(int userId);
}
