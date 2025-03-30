INSERT INTO doctor (id, first_name, last_name, license_number, working_hours) VALUES
(1, 'Jan', 'Kowalski', 'LN12345', '08:00-16:00'),
(2, 'Anna', 'Nowak', 'LN67890', '09:00-17:00'),
(3, 'Piotr', 'Wiśniewski', 'LN54321', '07:00-15:00'),
(4, 'Maria', 'Dąbrowska', 'LN09876', '10:00-18:00'),
(5, 'Tomasz', 'Lewandowski', 'LN13579', '11:00-19:00');

INSERT INTO employee_work_schedule (id, doctor_id, day_of_week, start_time, end_time) VALUES
(1, 1, 'MONDAY', '08:00', '14:00'),
(2, 1, 'TUESDAY', '09:00', '15:00'),
(3, 1, 'WEDNESDAY', '10:00', '16:00'),
(4, 1, 'THURSDAY', '08:00', '14:00'),
(5, 1, 'FRIDAY', '09:00', '15:00'),

(6, 2, 'MONDAY', '09:00', '17:00'),
(7, 2, 'TUESDAY', '10:00', '18:00'),
(8, 2, 'WEDNESDAY', '08:00', '16:00'),
(9, 2, 'THURSDAY', '07:00', '15:00'),
(10, 2, 'FRIDAY', '09:00', '17:00'),

(11, 3, 'MONDAY', '07:00', '13:00'),
(12, 3, 'TUESDAY', '08:00', '14:00'),
(13, 3, 'WEDNESDAY', '09:00', '15:00'),
(14, 3, 'THURSDAY', '10:00', '16:00'),
(15, 3, 'FRIDAY', '07:00', '13:00'),

(16, 4, 'MONDAY', '10:00', '18:00'),
(17, 4, 'TUESDAY', '11:00', '19:00'),
(18, 4, 'WEDNESDAY', '09:00', '17:00'),
(19, 4, 'THURSDAY', '08:00', '16:00'),
(20, 4, 'FRIDAY', '10:00', '18:00');
