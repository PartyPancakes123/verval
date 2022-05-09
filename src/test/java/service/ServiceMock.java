package service;

import domain.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceMock {
    Service service;
    @Mock
    StudentXMLRepository studentXMLRepo;
    @Mock
    HomeworkXMLRepository homeworkXMLRepo;
    @Mock
    GradeXMLRepository gradeXMLRepo;

    @BeforeAll
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new Service(studentXMLRepo, homeworkXMLRepo, gradeXMLRepo);
    }

    @Test
    public void testSaveStudent() {
        Mockito.when(studentXMLRepo.save(any(Student.class))).thenReturn(null);
        int returnValue = service.saveStudent("testStudentId1", "Test Test", 111);
        Mockito.verify(studentXMLRepo).save(new Student("testStudentId1", "Test Test", 111));

        assertEquals(1, returnValue);
    }

    @Test
    public void testSaveGradeForNonExistingStudent() {
        Mockito.when(studentXMLRepo.findOne("nonExistingStudentId")).thenReturn(null);
        int returnValue = service.saveGrade("nonExistingStudentId", "testHWId", 10, 1, "generic feedback for testing");
        assertEquals(-1, returnValue);
    }

    @Test
    public void testDeleteStudent() {
        Mockito.when(studentXMLRepo.delete("testStudentId")).thenReturn(new Student("testStudentId", "Test Test", 111));
        int returnValue = service.deleteStudent("testStudentId");
        assertEquals(1, returnValue);
    }
}
