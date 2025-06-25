package com.lokoko.global.auth.service;

import com.lokoko.global.auth.exception.StateValidationException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class StateService {
    private static final long STATE_EXPIRATION_MINUTES = 10;
    private final ConcurrentHashMap<String, Long> stateStore = new ConcurrentHashMap<>();

    public String generateState() {
        String state = UUID.randomUUID().toString();
        long expirationTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(STATE_EXPIRATION_MINUTES);
        stateStore.put(state, expirationTime);
        cleanExpiredStates();
        return state;
    }

    public void verify(String state) {

        if (state == null || state.isBlank()) {
            throw StateValidationException.required();
        }

        Long expirationTime = stateStore.remove(state);
        if (expirationTime == null) {
            throw StateValidationException.invalid();
        }

        if (System.currentTimeMillis() > expirationTime) {
            throw StateValidationException.expired();
        }
    }

    private void cleanExpiredStates() {
        long currentTime = System.currentTimeMillis();
        stateStore.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
}
