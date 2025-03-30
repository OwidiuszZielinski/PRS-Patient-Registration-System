package org.example.prspatientregistrationsystem.core.employeeworkschedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeWorkScheduleRepository extends JpaRepository<EmployeeWorkSchedule, Long> {
}
