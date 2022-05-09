package service;

import domain.Grade;
import domain.Homework;
import domain.Pair;
import domain.Student;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.GradeValidator;
import validation.HomeworkValidator;
import validation.StudentValidator;
import validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ServiceTest {
    Service service;
    StudentXMLRepository studentXMLRepo;
    HomeworkXMLRepository homeworkXMLRepo;
    GradeXMLRepository gradeXMLRepo;

    @BeforeAll
    public void setUp() throws Exception {
        Validator<Student> studentValidator = new StudentValidator();
        Validator<Homework> homeworkValidator = new HomeworkValidator();
        Validator<Grade> gradeValidator = new GradeValidator();

        studentXMLRepo = new StudentXMLRepository(studentValidator, "students.xml");
        homeworkXMLRepo = new HomeworkXMLRepository(homeworkValidator, "homework.xml");
        gradeXMLRepo = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(studentXMLRepo, homeworkXMLRepo, gradeXMLRepo);
    }

    @Test
    public void testSaveStudent() {
        service.saveStudent("testStudentId1", "Test Test", 111);
        assertNotNull(studentXMLRepo.findOne("testStudentId1"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "pTestId1", "pTestId1" })
    public void testParameterizedStudents(String id) {
        service.saveStudent(id, "PTest Test", 111);
        assertNotNull(studentXMLRepo.findOne(id));
    }

    @Test
    public void testSaveHomework() {
        service.saveHomework("testHWId1", "test HW description", 2, 1);
        assertNotNull(homeworkXMLRepo.findOne("testHWId1"));
    }

    @Test
    public void testSaveGrade() {
        service.saveStudent("testStudentId2", "Test Test", 111);
        service.saveHomework("testHWId2", "description for testSaveGrade()", 3, 1);
        service.saveGrade("testStudentId2", "testHWId2", 5, 2, "feedback for testSaveGrade()");
        assertNotNull(gradeXMLRepo.findOne(new Pair<>("testStudentId2", "testHWId2")));
    }

    @Test
    public void testSaveGradeForNonExistingStudent() {
        int returnValue = service.saveGrade("testStudentId3", "testHWId3", 10, 1, "generic feedback for testing");
        assertEquals(-1, returnValue);
    }

    @Test
    public void testDeleteStudent() {
        service.saveStudent("testStudentId4", "Test Test", 111);
        service.deleteStudent("testStudentId4");
        assertNull(studentXMLRepo.findOne("testStudentId4"));
    }

    @Test
    public void testSaveGradeForHWAfterDeadline() {
        service.saveStudent("testStudentId5", "Test Test", 111);
        service.saveHomework("testHWId3", "test HW for testing grade after deadline", 2, 1);
        double initialGradeVal = 5;
        service.saveGrade("testStudentId5", "testHWId3", initialGradeVal, 3, "generic feedback for testing");
        double gradeAfterAdjustment = gradeXMLRepo.findOne(new Pair<>("testStudentId5", "testHWId3")).getGrade();
        assertTrue(gradeAfterAdjustment < initialGradeVal);
    }


    @AfterAll
    public void cleanup() {
        service.deleteStudent("testStudentId1");
        service.deleteStudent("testStudentId2");
        service.deleteStudent("testStudentId3");
        service.deleteStudent("testStudentId4");
        service.deleteStudent("testStudentId5");
        service.deleteStudent("pTestId1");
        service.deleteStudent("pTestId2");

        service.deleteHomework("testHWId1");
        service.deleteHomework("testHWId2");
        service.deleteHomework("testHWId3");

        gradeXMLRepo.delete(new Pair<>("testStudentId2", "testHWId2"));
        gradeXMLRepo.delete(new Pair<>("testStudentId5", "testHWId3"));
    }
}