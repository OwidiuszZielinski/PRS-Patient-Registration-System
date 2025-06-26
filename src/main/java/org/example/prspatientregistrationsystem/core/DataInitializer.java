package org.example.prspatientregistrationsystem.core;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final JdbcTemplate jdbc;

    public DataInitializer(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void run(ApplicationArguments args) {
        createUniqueUsernameConstraint();

        if (isTableEmpty("app_user")) {
            insertAppUsers();
        }

        if (isTableEmpty("doctor")) {
            insertDoctors();
        }

        if (isTableEmpty("visit_entity")) {
            insertVisits();
        }

        if (isTableEmpty("patient")) {
            insertPatients();
        }
    }

    private boolean isTableEmpty(String tableName) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
        return count == null || count == 0;
    }

    private void createUniqueUsernameConstraint() {
        String checkConstraint = """
            DO $$
            BEGIN
                IF NOT EXISTS (
                    SELECT 1 FROM pg_constraint WHERE conname = 'unique_username'
                ) THEN
                    ALTER TABLE app_user ADD CONSTRAINT unique_username UNIQUE (username);
                END IF;
            END $$;
        """;
        jdbc.execute(checkConstraint);
    }

    private void insertAppUsers() {
        jdbc.execute("""
            INSERT INTO app_user (email, password, username, role) VALUES
            ('admin@example.com',   '$2a$10$GH/.ZOdPa42xOpzNHFYY4ulmEUY1fMfrjxFcqwkR2tUTPv6Or2KsC', 'admin123',   'ADMIN'),
            ('doctor@example.com',  '$2a$10$GH/.ZOdPa42xOpzNHFYY4ulmEUY1fMfrjxFcqwkR2tUTPv6Or2KsC', 'doctor123',  'DOCTOR'),
            ('patient@example.com', '$2a$10$GH/.ZOdPa42xOpzNHFYY4ulmEUY1fMfrjxFcqwkR2tUTPv6Or2KsC', 'patient123', 'PATIENT'),
            ('waiting@example.com', '$2a$10$GH/.ZOdPa42xOpzNHFYY4ulmEUY1fMfrjxFcqwkR2tUTPv6Or2KsC', 'waiting123', 'WAITING_ROOM');
        """);
    }

    private void insertDoctors() {
        jdbc.execute("""
            INSERT INTO doctor (first_name, office_id, last_name, license_number) VALUES
            ('Jan', 1,  'Kowalski', 'DOC123456'),
            ('Anna', 2, 'Nowak', 'DOC654321'),
            ('Piotr', 3, 'Wiśniewski', 'DOC987654'),
            ('Maria', 4, 'Wójcik', 'DOC456789'),
            ('Andrzej', 5, 'Kowalczyk', 'DOC321654');
        """);
    }

    private void insertVisits() {
        jdbc.execute("""
            INSERT INTO visit_entity (doctor_name, patient, date, description) VALUES
            ('Anna Kowalska', 'Jan Nowak', '2025-06-01 10:00:00', 'Wizyta kontrolna'),
            ('Piotr Zieliński', 'Maria Wiśniewska', '2025-06-02 11:30:00', 'Ból pleców'),
            ('Katarzyna Wójcik', 'Tomasz Lewandowski', '2025-06-03 09:15:00', 'Wystawienie recepty'),
            ('Michał Nowak', 'Anna Dąbrowska', '2025-06-04 14:00:00', 'Szczepienie'),
            ('Ewa Kaczmarek', 'Paweł Kwiatkowski', '2025-06-05 08:45:00', 'Badania okresowe');
        """);
    }

    private void insertPatients() {
        jdbc.execute("""
            INSERT INTO patient (first_name, last_name, email, phone_number, identification_number, birth_date) VALUES
            ('Krzysztof',   'Jabłoński',   'krzysztof.jablonski@example.com',  '+48 506 789 012', '67890123456', '1980-05-15'),
            ('Agnieszka',   'Piotrowska',  'agnieszka.piotrowska@example.com', '+48 507 890 123', '78901234567', '1985-07-22'),
            ('Łukasz',      'Nowicki',     'lukasz.nowicki@example.com',       '+48 508 901 234', '89012345678', '1990-11-03'),
            ('Katarzyna',   'Mazur',       'katarzyna.mazur@example.com',      '+48 509 012 345', '90123456789', '1978-02-28'),
            ('Paweł',       'Szymański',   'pawel.szymański@example.com',      '+48 510 123 456', '01234567890', '1972-09-10');
        """);
    }
} 