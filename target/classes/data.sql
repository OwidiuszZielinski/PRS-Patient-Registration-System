-- Dodawanie 10 lekarzy
INSERT INTO doctor (first_name, last_name, license_number)
VALUES ('Jan', 'Kowalski', 'DOC123456'),
       ('Anna', 'Nowak', 'DOC654321'),
       ('Piotr', 'Wiśniewski', 'DOC987654'),
       ('Maria', 'Wójcik', 'DOC456789'),
       ('Andrzej', 'Kowalczyk', 'DOC321654'),
       ('Katarzyna', 'Kamińska', 'DOC789456'),
       ('Tomasz', 'Lewandowski', 'DOC654987'),
       ('Barbara', 'Dąbrowska', 'DOC321987'),
       ('Marcin', 'Zieliński', 'DOC987321'),
       ('Ewa', 'Szymańska', 'DOC456123');

-- Dodawanie grafików pracy dla każdego lekarza na każdy dzień tygodnia
-- Każdy lekarz ma różne godziny pracy w zależności od dnia tygodnia

-- Lekarz 1
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '08:00:00', '16:00:00', 1),
       ('TUESDAY', '09:00:00', '17:00:00', 1),
       ('WEDNESDAY', '08:00:00', '14:00:00', 1),
       ('THURSDAY', '10:00:00', '18:00:00', 1),
       ('FRIDAY', '08:00:00', '16:00:00', 1),
       ('SATURDAY', '09:00:00', '13:00:00', 1),
       ('SUNDAY', NULL, NULL, 1);

-- Lekarz 2
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '07:00:00', '15:00:00', 2),
       ('TUESDAY', '07:00:00', '15:00:00', 2),
       ('WEDNESDAY', '07:00:00', '15:00:00', 2),
       ('THURSDAY', '07:00:00', '15:00:00', 2),
       ('FRIDAY', '07:00:00', '13:00:00', 2),
       ('SATURDAY', NULL, NULL, 2),
       ('SUNDAY', NULL, NULL, 2);

-- Lekarz 3
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '12:00:00', '20:00:00', 3),
       ('TUESDAY', '12:00:00', '20:00:00', 3),
       ('WEDNESDAY', '12:00:00', '20:00:00', 3),
       ('THURSDAY', '12:00:00', '20:00:00', 3),
       ('FRIDAY', '10:00:00', '18:00:00', 3),
       ('SATURDAY', '09:00:00', '14:00:00', 3),
       ('SUNDAY', NULL, NULL, 3);

-- Lekarz 4
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '08:00:00', '16:00:00', 4),
       ('TUESDAY', '08:00:00', '16:00:00', 4),
       ('WEDNESDAY', NULL, NULL, 4),
       ('THURSDAY', '08:00:00', '16:00:00', 4),
       ('FRIDAY', '08:00:00', '16:00:00', 4),
       ('SATURDAY', '08:00:00', '12:00:00', 4),
       ('SUNDAY', NULL, NULL, 4);

-- Lekarz 5
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '09:00:00', '17:00:00', 5),
       ('TUESDAY', '09:00:00', '17:00:00', 5),
       ('WEDNESDAY', '09:00:00', '17:00:00', 5),
       ('THURSDAY', '09:00:00', '17:00:00', 5),
       ('FRIDAY', '09:00:00', '15:00:00', 5),
       ('SATURDAY', NULL, NULL, 5),
       ('SUNDAY', NULL, NULL, 5);

-- Lekarz 6
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '10:00:00', '18:00:00', 6),
       ('TUESDAY', '08:00:00', '14:00:00', 6),
       ('WEDNESDAY', '10:00:00', '18:00:00', 6),
       ('THURSDAY', '08:00:00', '14:00:00', 6),
       ('FRIDAY', '10:00:00', '18:00:00', 6),
       ('SATURDAY', '09:00:00', '13:00:00', 6),
       ('SUNDAY', NULL, NULL, 6);

-- Lekarz 7
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '07:30:00', '15:30:00', 7),
       ('TUESDAY', '07:30:00', '15:30:00', 7),
       ('WEDNESDAY', '07:30:00', '15:30:00', 7),
       ('THURSDAY', '07:30:00', '15:30:00', 7),
       ('FRIDAY', '07:30:00', '13:30:00', 7),
       ('SATURDAY', NULL, NULL, 7),
       ('SUNDAY', NULL, NULL, 7);

-- Lekarz 8
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '13:00:00', '21:00:00', 8),
       ('TUESDAY', '13:00:00', '21:00:00', 8),
       ('WEDNESDAY', '13:00:00', '21:00:00', 8),
       ('THURSDAY', '13:00:00', '21:00:00', 8),
       ('FRIDAY', '11:00:00', '19:00:00', 8),
       ('SATURDAY', '10:00:00', '15:00:00', 8),
       ('SUNDAY', NULL, NULL, 8);

-- Lekarz 9ó
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '08:00:00', '12:00:00', 9),
       ('TUESDAY', '12:00:00', '20:00:00', 9),
       ('WEDNESDAY', '08:00:00', '12:00:00', 9),
       ('THURSDAY', '12:00:00', '20:00:00', 9),
       ('FRIDAY', '08:00:00', '12:00:00', 9),
       ('SATURDAY', NULL, NULL, 9),
       ('SUNDAY', NULL, NULL, 9);

-- Lekarz 10
INSERT INTO employee_work_schedule (day_of_week, start_time, end_time, doctor_id)
VALUES ('MONDAY', '09:00:00', '17:00:00', 10),
       ('TUESDAY', '09:00:00', '17:00:00', 10),
       ('WEDNESDAY', '09:00:00', '17:00:00', 10),
       ('THURSDAY', '09:00:00', '17:00:00', 10),
       ('FRIDAY', '09:00:00', '15:00:00', 10),
       ('SATURDAY', '09:00:00', '13:00:00', 10),
       ('SUNDAY', NULL, NULL, 10);