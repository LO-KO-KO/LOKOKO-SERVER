package com.lokoko.global.utils;

import com.lokoko.domain.user.entity.User;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.global.auth.exception.AdminPermissionRequiredException;

public class AdminValidator {
    public static void validateUserRole(User user) {
        if (!(user.getRole() == Role.ADMIN)) {
            throw new AdminPermissionRequiredException();
        }
    }
}
