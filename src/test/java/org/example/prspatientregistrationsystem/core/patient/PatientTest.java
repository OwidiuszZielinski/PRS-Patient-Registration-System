package org.example.prspatientregistrationsystem.core.patient;

import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PatientTest {
    @Test void testBuilderAndGetters() {
        AppUser user = new AppUser();
        Patient patient = Patient.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jan@kowalski.pl")
                .phoneNumber("123456789")
                .identificationNumber("90010112345")
                .birthDate(LocalDate.of(1990,1,1))
                .appUser(user)
                .build();
        assertEquals(1L, patient.getId());
        assertEquals("Jan", patient.getFirstName());
        assertEquals("Kowalski", patient.getLastName());
        assertEquals("jan@kowalski.pl", patient.getEmail());
        assertEquals("123456789", patient.getPhoneNumber());
        assertEquals("90010112345", patient.getIdentificationNumber());
        assertEquals(LocalDate.of(1990,1,1), patient.getBirthDate());
        assertEquals(user, patient.getAppUser());
    }
    @Test void testSetters() {
        Patient patient = new Patient();
        patient.setId(2L);
        patient.setFirstName("Anna");
        patient.setLastName("Nowak");
        assertEquals(2L, patient.getId());
        assertEquals("Anna", patient.getFirstName());
        assertEquals("Nowak", patient.getLastName());
    }
    @Test void testEqualsAndHashCode() {
        Patient p1 = new Patient();
        p1.setId(1L);
        Patient p2 = new Patient();
        p2.setId(1L);
        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
} 