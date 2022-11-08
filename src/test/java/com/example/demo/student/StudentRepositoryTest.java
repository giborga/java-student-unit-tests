package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// methods provided by JPA are already tested
@DataJpaTest //will autowire everything, autowire student repo, spin up db
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach // will clean everything after each test
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    //this test needs to be connected to the h2 database
    void itShouldCheckIfStudentEmailExist() { // we don't want to hit local database
        // given
        String email = "pali@gmail.com"; // create new instance
        Student student = new Student(
                "Inha", email, Gender.FEMALE
        );

        underTest.save(student); // StudentRepository: save student to db

        // when
        boolean exists = underTest.selectExistsEmail(email); // check if email exists in db

        // then - the result we want
        assertThat(exists).isTrue(); // assert that it exists (that exists returned True)
    }

    @Test // + student is not created -> email does not exist in student repo
    void itShouldCheckIfStudentEmailNotExist() {  // test needs to be connected to the h2 database

        // given
        String email = "pali@gmail.com";

        // when - we test selectExistsEmail because it's custom
        boolean exists = underTest.selectExistsEmail(email); // check if email exists in db

        // then - the result we want
        assertThat(exists).isFalse(); // assert that selectExistsEmail returns false
    }
}