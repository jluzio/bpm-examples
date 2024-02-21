package com.example.spring.bpm.playground.process;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractProcessTest {

  /**
   * @see ProcessTestHelper#resetAssertions()
   */
  @BeforeAll
  @AfterAll
  static void resetAssertions() {
    ProcessTestHelper.resetAssertions();
  }

  /**
   * @see ProcessTestHelper#resetMocks()
   */
  @AfterAll
  static void resetMocks() {
    ProcessTestHelper.resetMocks();
  }

}
