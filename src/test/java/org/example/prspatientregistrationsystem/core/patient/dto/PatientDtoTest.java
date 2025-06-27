package org.example.prspatientregistrationsystem.core.patient.dto;

import org.example.prspatientregistrationsystem.core.patient.Patient;
import org.example.prspatientregistrationsystem.core.user.AppUser;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class PatientDtoTest {
    @Test void testBuilderAndGetters() {
        PatientDto dto = PatientDto.builder()
                .id(1L)
                .firstname("Jan")
                .lastname("Kowalski")
                .email("jan@kowalski.pl")
                .phoneNumber("123456789")
                .identificationNumber("90010112345")
                .birthDate(LocalDate.of(1990,1,1))
                .appUser(null)
                .build();
        assertEquals(1L, dto.id());
        assertEquals("Jan", dto.firstname());
        assertEquals("Kowalski", dto.lastname());
        assertEquals("jan@kowalski.pl", dto.email());
        assertEquals("123456789", dto.phoneNumber());
        assertEquals("90010112345", dto.identificationNumber());
        assertEquals(LocalDate.of(1990,1,1), dto.birthDate());
        assertNull(dto.appUser());
    }
    @Test void testMapToPatientDto() {
        Patient patient = new Patient();
        patient.setId(2L);
        patient.setFirstName("Anna");
        patient.setLastName("Nowak");
        patient.setEmail("anna@nowak.pl");
        patient.setPhoneNumber("987654321");
        patient.setIdentificationNumber("80010154321");
        patient.setBirthDate(LocalDate.of(1980,1,1));
        patient.setAppUser(null);
        PatientDto dto = PatientDto.mapToPatientDto(patient);
        assertEquals("Anna", dto.firstname());
        assertEquals("Nowak", dto.lastname());
        assertEquals("anna@nowak.pl", dto.email());
        assertEquals("987654321", dto.phoneNumber());
        assertEquals("80010154321", dto.identificationNumber());
        assertEquals(LocalDate.of(1980,1,1), dto.birthDate());
        assertNull(dto.appUser());
    }
    @Test void testToPatient() {
        PatientDto dto = PatientDto.builder()
                .id(3L)
                .firstname("Piotr")
                .lastname("Zielinski")
                .email("piotr@zielinski.pl")
                .phoneNumber("111222333")
                .identificationNumber("70010111111")
                .birthDate(LocalDate.of(1970,1,1))
                .appUser(null)
                .build();
        Patient patient = dto.toPatient();
        assertEquals("Piotr", patient.getFirstName());
        assertEquals("Zielinski", patient.getLastName());
        assertEquals("piotr@zielinski.pl", patient.getEmail());
        assertEquals("111222333", patient.getPhoneNumber());
        assertEquals("70010111111", patient.getIdentificationNumber());
        assertEquals(LocalDate.of(1970,1,1), patient.getBirthDate());
        assertNull(patient.getAppUser());
    }
    @Test void testEqualsAndHashCode() {
        PatientDto dto1 = PatientDto.builder().id(1L).firstname("A").build();
        PatientDto dto2 = PatientDto.builder().id(1L).firstname("A").build();
        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
    @Test void testNotEquals() {
        PatientDto dto1 = PatientDto.builder().id(1L).firstname("A").build();
        PatientDto dto2 = PatientDto.builder().id(2L).firstname("B").build();
        assertNotEquals(dto1, dto2);
    }
    @Test void testNullFields() {
        PatientDto dto = PatientDto.builder().build();
        assertNull(dto.firstname());
        assertNull(dto.lastname());
        assertNull(dto.email());
        assertNull(dto.phoneNumber());
        assertNull(dto.identificationNumber());
        assertNull(dto.birthDate());
        assertNull(dto.appUser());
    }
    @Test void testToPatientWithNulls() {
        PatientDto dto = PatientDto.builder().build();
        Patient patient = dto.toPatient();
        assertNull(patient.getFirstName());
        assertNull(patient.getLastName());
        assertNull(patient.getEmail());
        assertNull(patient.getPhoneNumber());
        assertNull(patient.getIdentificationNumber());
        assertNull(patient.getBirthDate());
        assertNull(patient.getAppUser());
    }
    @Test void testMapToPatientDtoWithNullPatient() {
        assertThrows(NullPointerException.class, () -> PatientDto.mapToPatientDto(null));
    }
    @Test void testToString() {
        PatientDto dto = PatientDto.builder().id(5L).firstname("X").build();
        assertTrue(dto.toString().contains("X"));
    }
    @Test void testWithAppUser() {
        AppUser user = new AppUser();
        PatientDto dto = PatientDto.builder().appUser(user).build();
        assertEquals(user, dto.appUser());
    }
} 