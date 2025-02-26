package com.assignment_two_starter.service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = new HashSet<>();


    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }


    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
