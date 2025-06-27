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

        if (isTableEmpty("service_entity")) {
            insertServices();
        }

        if (isTableEmpty("visit_entity")) {
            insertVisits();
        }

        if (isTableEmpty("patient")) {
            insertPatients();
        }
    }

    boolean isTableEmpty(String tableName) {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM " + tableName, Integer.class);
        return count == null || count == 0;
    }

    void createUniqueUsernameConstraint() {
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
            ('John', 1,  'Smith', 'DOC123456'),
            ('Anna', 2, 'Johnson', 'DOC654321'),
            ('Peter', 3, 'Williams', 'DOC987654'),
            ('Mary', 4, 'Brown', 'DOC456789'),
            ('Andrew', 5, 'Davis', 'DOC321654');
        """);
    }

    private void insertServices() {
        jdbc.execute("""
            INSERT INTO service_entity (name, price, description) VALUES
            ('ECG', 150.00, 'Electrocardiography - heart examination'),
            ('Abdominal Ultrasound', 200.00, 'Ultrasonographic examination of the abdominal cavity'),
            ('Cardiac Ultrasound', 250.00, 'Echocardiography - heart examination'),
            ('Chest X-Ray', 120.00, 'Chest X-ray examination'),
            ('Blood Test', 80.00, 'Basic laboratory blood tests'),
            ('Urine Test', 60.00, 'Urine analysis'),
            ('Spirometry', 100.00, 'Lung function test'),
            ('ECG Holter', 300.00, '24-hour ECG monitoring'),
            ('Blood Pressure Holter', 250.00, '24-hour blood pressure monitoring'),
            ('Colonoscopy', 800.00, 'Endoscopic examination of the large intestine'),
            ('Gastroscopy', 600.00, 'Endoscopic examination of the stomach'),
            ('CT Scan', 1200.00, 'Computed tomography of selected body part'),
            ('MRI Scan', 1500.00, 'Magnetic resonance imaging of selected body part');
        """);
    }

    private void insertVisits() {
        jdbc.execute("""
            INSERT INTO visit_entity (doctor_name, patient, date, description) VALUES
            ('Anna Johnson', 'John Smith', '2025-06-01 10:00:00', 'Control visit'),
            ('Peter Williams', 'Mary Brown', '2025-06-02 11:30:00', 'Back pain'),
            ('Mary Brown', 'Thomas Wilson', '2025-06-03 09:15:00', 'Prescription'),
            ('Andrew Davis', 'Anna Davis', '2025-06-04 14:00:00', 'Vaccination'),
            ('John Smith', 'Paul Miller', '2025-06-05 08:45:00', 'Periodic examination');
        """);
    }

    private void insertPatients() {
        jdbc.execute("""
            INSERT INTO patient (first_name, last_name, email, phone_number, identification_number, birth_date) VALUES
            ('Christopher',   'Johnson',   'christopher.johnson@example.com',  '+48 506 789 012', '67890123456', '1980-05-15'),
            ('Agnes',   'Peters',  'agnes.peters@example.com', '+48 507 890 123', '78901234567', '1985-07-22'),
            ('Luke',      'Wilson',     'luke.wilson@example.com',       '+48 508 901 234', '89012345678', '1990-11-03'),
            ('Catherine',   'Miller',       'catherine.miller@example.com',      '+48 509 012 345', '90123456789', '1978-02-28'),
            ('Paul',       'Davis',   'paul.davis@example.com',      '+48 510 123 456', '01234567890', '1972-09-10');
        """);
    }
} 