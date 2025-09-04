package femcoders25.mykitchen_hub.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TokenBlacklistService {

    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
        log.debug("Token blacklisted: {}", token.substring(0, Math.min(20, token.length())) + "...");
    }

    public boolean isTokenBlacklisted(String token) {
        return !blacklistedTokens.contains(token);
    }

    public void removeExpiredTokens() {
        blacklistedTokens.clear();
        log.debug("Cleared blacklisted tokens");
    }
}
