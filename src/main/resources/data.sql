-- Wstawienie lekarzy
INSERT INTO doctor (id, first_name, last_name, license_number, working_hours)
VALUES
    (1, 'Jan', 'Kowalski', 'DOC12345', '08:00-16:00'),
    (2, 'Anna', 'Nowak', 'DOC67890', '10:00-18:00');

-- Wstawienie harmonogramu pracy
INSERT INTO employee_work_schedule (id, end_time, start_time, doctor_id, day_of_week)
VALUES
    (1, '16:00', '08:00', 1, 'MONDAY'),
    (2, '18:00', '10:00', 2, 'TUESDAY');

-- Wstawienie powiązania lekarzy z harmonogramami pracy
INSERT INTO doctor_employee_work_schedules (employee_work_schedules_id, doctor_id)
VALUES
    (1, 1),
    (2, 2);
