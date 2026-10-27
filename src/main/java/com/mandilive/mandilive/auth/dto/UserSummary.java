package com.mandilive.mandilive.auth.dto;

import com.mandilive.mandilive.user.Role;

public record UserSummary(Long id, String name, String email, Role role) {
}
