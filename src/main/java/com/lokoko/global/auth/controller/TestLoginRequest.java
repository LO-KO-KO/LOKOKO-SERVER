package com.lokoko.global.auth.controller;

import jakarta.validation.constraints.NotNull;

public record TestLoginRequest(
        @NotNull Long userId
) {
}
