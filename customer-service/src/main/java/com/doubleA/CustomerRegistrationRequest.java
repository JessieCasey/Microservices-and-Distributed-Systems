package com.doubleA;

public record CustomerRegistrationRequest(
        String firstname,
        String lastname,
        String email) {
}
