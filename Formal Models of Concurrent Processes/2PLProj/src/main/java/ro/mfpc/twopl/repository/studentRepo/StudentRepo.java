package ro.mfpc.twopl.repository.studentRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.mfpc.twopl.model.student.Student;

public interface StudentRepo extends JpaRepository<Student, Integer> {
}
