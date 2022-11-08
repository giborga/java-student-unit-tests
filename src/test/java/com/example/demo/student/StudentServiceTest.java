package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // this extension is used to open and close mocks
class StudentServiceTest {

    // reference to student repo. need to mock it
    @Mock private StudentRepository studentRepository; // we tested it and now we know that all methods there work and we can mock it
    //private AutoCloseable autoCloseable;
    private StudentService underTest;

    @BeforeEach // will get a fresh instance for student every test
    void setUp() {
        // if we have more than one mock it will initialise all mocks in this class
        //autoCloseable = MockitoAnnotations.openMocks(this); // in order to initialise StudentRepository mock use Mockito
        underTest = new StudentService(studentRepository);
    }

    //@AfterEach // to close the resource after the test
    //void tearDown() throws Exception {
    //    autoCloseable.close();
    //}

    // we don't test real student repo (doesnt create db / tables etc.)
    @Test
    void GetAllStudentsSuccess() {
        // when
        underTest.getAllStudents();
        // then: verify that findAll() method from JPA repository was invoked. Ex. if replace findAll() with deleteAll() - test fails.
        // because getAllStudents from Service invokes getAllStudents from Repository who invokes findAll from JPA
        verify(studentRepository).findAll();

    }

    @Test // check that the student saved is the student passed
    void addStudentSuccess() {

        // when
        Student student = new Student("Inha", "pali@gmail.com", Gender.FEMALE);
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository) // we want to verify that repository was called with save() method + does verify return?
                .save(studentArgumentCaptor.capture()); // we want to capture the actual student that was passed inside the save method: studentRepository.save(student);

        // assert
        Student capturedStudent = studentArgumentCaptor.getValue(); // student should be == to the one that is got by Captor in the end of the test
        System.out.println(capturedStudent.getId());
        assertThat(capturedStudent).isEqualTo(student); // + diff b/w isEqualTo and equals? / what does assertThat() return?
    }


    // + syntax of tests...?
    @Test // when email is taken -> our code should throw an exception
    void throwsExceptionWhenStudentEmailTaken() {
        // when
        // then
        Student student = new Student("Inha", "pali@gmail.com", Gender.FEMALE); // given this student

        given(studentRepository
                .selectExistsEmail(anyString())) // instead of student.getEmail() use any string - doesn't matter
                .willReturn(true); // enforces that in any case selectExistsEmail returns true

        // assert: the result we expect
        // in SS existsEmail is false because this email does not exist in repo
        // exception will be thrown only if existsEmail is true (cant add new student because email is taken)
        assertThatThrownBy(() -> underTest.addStudent(student)) // assertThatThrownBy() takes lambda
                .isInstanceOf(BadRequestException.class)// throw new BadRequestException
                .hasMessageContaining("Email " + student.getEmail() + " taken"); // if message is copied - it should be exact copy from SS and in case of mistake in the message will throw "Email pali@gmail.com taken" that we dont want to

        //also repo .save() method is never called in case of exception
        verify(studentRepository, never()).save(any()); // says save is never saves any student :))
    }


    @Test // + check that the student deleted is the student passed??
    void deleteStudentSuccess() {

        // when
        Student student = new Student("Inha", "pali@gmail.com", Gender.FEMALE);
        student.setId(1L); // set random id
        System.out.println(student.getId());

        studentRepository.deleteById(student.getId());

        verify(studentRepository).deleteById(student.getId());

        // assert
        assertThat(1L).isEqualTo(student.getId());
    }


    @Test // + check it!
    void DeleteThrowsExceptionWhenStudentIdDoesNotExist() {

        // when
        // then
        Student student = new Student("Inha", "pali@gmail.com", Gender.FEMALE);
        student.setId(0L); // set random id

        given(studentRepository
                .existsById(anyLong()))
                .willReturn(false); // enforces that in any case existsById returns false

        // assert: the result we expect: throw exception if id is not there
        assertThatThrownBy(() -> underTest.deleteStudent(student.getId())) // assertThatThrownBy() takes lambda
                .isInstanceOf(StudentNotFoundException.class)// throw new StudentNotFoundException
                .hasMessageContaining("Student with id " + student.getId() + " does not exists");

        //also repo .deleteById() method is never called in case of exception
        verify(studentRepository, never()).deleteById(any()); // says save is never deletes any student :))

    }
}