INSERT INTO doctor (first_name, office_id, last_name, license_number)
VALUES ('Jan', 1,  'Kowalski', 'DOC123456'),
       ('Anna', 2, 'Nowak', 'DOC654321'),
       ('Piotr', 3, 'Wiśniewski', 'DOC987654'),
       ('Maria', 4, 'Wójcik', 'DOC456789'),
       ('Andrzej', 5, 'Kowalczyk', 'DOC321654'),
       ('Andrzej', 5, 'Kowalczyk', 'DOC321654');

INSERT INTO visit_entity (doctor_name, patient, date, description) VALUES
('Anna Kowalska', 'Jan Nowak', '2025-06-01 10:00:00', 'Wizyta kontrolna'),
('Piotr Zieliński', 'Maria Wiśniewska', '2025-06-02 11:30:00', 'Ból pleców'),
('Katarzyna Wójcik', 'Tomasz Lewandowski', '2025-06-03 09:15:00', 'Wystawienie recepty'),
('Michał Nowak', 'Anna Dąbrowska', '2025-06-04 14:00:00', 'Szczepienie'),
('Ewa Kaczmarek', 'Paweł Kwiatkowski', '2025-06-05 08:45:00', 'Badania okresowe');
