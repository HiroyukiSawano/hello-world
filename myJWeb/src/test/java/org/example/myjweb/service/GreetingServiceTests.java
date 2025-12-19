package org.example.myjweb.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GreetingServiceTests {

    private final GreetingService greetingService = new GreetingService();

    @Test
    void shouldReturnDefaultGreetingWhenNameMissing() {
        assertEquals("Hello", greetingService.greet(null));
        assertEquals("Hello", greetingService.greet(""));
        assertEquals("Hello", greetingService.greet("   "));
    }

    @Test
    void shouldReturnGreetingWithName() {
        assertEquals("Hello, Bob", greetingService.greet("Bob"));
        assertEquals("Hello, Bob", greetingService.greet("  Bob  "));
    }
}
