package org.example.myjweb.service;

import org.springframework.util.StringUtils;

final class ServiceValidator {

    private ServiceValidator() {
    }

    static void requireId(Long id, String message) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    static void requireText(String text, String message) {
        if (!StringUtils.hasText(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    static void requireObject(Object target, String message) {
        if (target == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
