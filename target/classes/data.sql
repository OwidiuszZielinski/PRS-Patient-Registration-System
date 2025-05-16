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
--
--INSERT INTO employee_work_schedule (doctor_id, schedule_date, is_working_day, is_vacation, start_time, end_time)
--VALUES (1, '2024-05-01', true, false, '08:00:00', '16:00:00');
--
---- Dzień urlopu
--INSERT INTO employee_work_schedule (doctor_id, schedule_date, is_working_day, is_vacation, start_time, end_time)
--VALUES (1, '2024-05-02', false, true, NULL, NULL);
--
---- Dzień wolny (nie urlop)
--INSERT INTO employee_work_schedule (doctor_id, schedule_date, is_working_day, is_vacation, start_time, end_time)
--VALUES (1, '2024-05-03', false, false, NULL, NULL);
