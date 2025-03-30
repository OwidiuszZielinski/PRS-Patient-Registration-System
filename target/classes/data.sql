CREATE TABLE doctor (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    license_number VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE doctor_working_hours (
    doctor_id INT NOT NULL,
    day_of_week VARCHAR(20),
    working_hours VARCHAR(100),
    PRIMARY KEY (doctor_id, day_of_week),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id) ON DELETE CASCADE
);


INSERT INTO doctor (first_name, last_name, license_number) VALUES
('Jan', 'Kowalski', '12345'),
('Anna', 'Nowak', '67890'),
('Tomasz', 'Wiśniewski', '11111'),
('Katarzyna', 'Dąbrowska', '22222'),
('Piotr', 'Lewandowski', '33333'),
('Agnieszka', 'Zielińska', '44444'),
('Michał', 'Szymański', '55555'),
('Magdalena', 'Woźniak', '66666'),
('Paweł', 'Kamiński', '77777'),
('Ewa', 'Kaczmarek', '88888');

