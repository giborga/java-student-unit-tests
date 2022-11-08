package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
class DemoApplicationTests {
	Calculator underTest = new Calculator();

	@Test //JUnit
	void itShouldAddTwoNumbers() { // test method
		// given
		int numberOne = 20;
		int numberTwo = 30;

		//when
		int result = underTest.add(numberOne, numberTwo); // method from Calculator class

		//then
		int expected = 51 ;
		assertThat(result).isEqualTo(expected);
	}

	class Calculator { // will "underTest" variable
		int add(int a, int b) { // method
			return a + b;
			}
	}

}
