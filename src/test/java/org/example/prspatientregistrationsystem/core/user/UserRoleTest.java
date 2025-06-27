package org.example.prspatientregistrationsystem.core.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {
    @Test void testEnumValues() {
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
        assertEquals(UserRole.DOCTOR, UserRole.valueOf("DOCTOR"));
        assertEquals(UserRole.PATIENT, UserRole.valueOf("PATIENT"));
        assertEquals(UserRole.WAITING_ROOM, UserRole.valueOf("WAITING_ROOM"));
        assertEquals(4, UserRole.values().length);
    }
} 