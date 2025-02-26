package com.assignment_two_starter.service;

import com.assignment_two_starter.model.RefreshToken;
import com.assignment_two_starter.model.Customer;
import com.assignment_two_starter.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${jwt.refresh.expiration}") String refreshTokenDurationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenDurationMs = Long.parseLong(refreshTokenDurationMs);
    }

    public RefreshToken createRefreshToken(Customer customer) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCustomer(customer);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByCustomer(Customer customer) {
        refreshTokenRepository.deleteByCustomer(customer);
    }

    public boolean isRefreshTokenExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }
}
