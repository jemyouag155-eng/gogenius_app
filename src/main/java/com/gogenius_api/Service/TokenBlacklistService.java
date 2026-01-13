package com.gogenius_api.Service;

import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {
private final Set<String> blacklistedTokens = new HashSet<>();
    
    public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
        System.out.println("⚫ Token ajouté à la blacklist");
    }
    
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
    
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }
}
