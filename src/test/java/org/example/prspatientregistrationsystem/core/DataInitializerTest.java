package org.example.prspatientregistrationsystem.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.ApplicationArguments;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DataInitializerTest {
    private JdbcTemplate jdbc;
    private DataInitializer initializer;

    @BeforeEach
    void setUp() {
        jdbc = mock(JdbcTemplate.class);
        initializer = new DataInitializer(jdbc);
    }

    @Test
    void testIsTableEmptyTrue() {
        when(jdbc.queryForObject(anyString(), eq(Integer.class))).thenReturn(0);
        assertTrue(initializer.isTableEmpty("app_user"));
    }

    @Test
    void testIsTableEmptyFalse() {
        when(jdbc.queryForObject(anyString(), eq(Integer.class))).thenReturn(5);
        assertFalse(initializer.isTableEmpty("doctor"));
    }

    @Test
    void testCreateUniqueUsernameConstraint() {
        doNothing().when(jdbc).execute(anyString());
        assertDoesNotThrow(() -> initializer.createUniqueUsernameConstraint());
        verify(jdbc, times(1)).execute(anyString());
    }
} 