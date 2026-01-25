package com.example.service;

import com.example.domain.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserServiceTest {

    private static EntityManagerFactory emf;
    private UserService userService;

    @BeforeAll
    static void initEntityManagerFactory() {
        emf = Persistence.createEntityManagerFactory("testPU");
    }

    @AfterAll
    static void closeEntityManagerFactory() {
        if (emf != null) {
            emf.close();
        }
    }

    @BeforeEach
    void initService() {
        userService = new UserService();
        userService.setEmf(emf);
        userService.setProducer(null);
    }

    @Test
    void registerAndFindUser() {
        User user = new User("Alice", "alice@example.com", "secret");

        User created = userService.register(user);

        assertNotNull(created.getId(), "User id should be generated");
        User reloaded = userService.findById(created.getId());
        assertNotNull(reloaded);
        assertEquals("Alice", reloaded.getName());
        assertEquals("alice@example.com", reloaded.getEmail());
    }
}
