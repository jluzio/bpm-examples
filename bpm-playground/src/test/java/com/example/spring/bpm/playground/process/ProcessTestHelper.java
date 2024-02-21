package com.example.spring.bpm.playground.process;

import lombok.experimental.UtilityClass;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.test.mock.Mocks;

@UtilityClass
public class ProcessTestHelper {

  /**
   * <p>BpmnAwareTests ProcessEngine is stored in a ThreadLocal.</p>
   * <p>If running multiple tests on same thread, it must be reset after used,
   * since currently it isn't cleared on tests.</p>
   */
  public static void resetAssertions() {
    BpmnAwareTests.reset();
  }

  /**
   * <p>Mocks variables are stored in a ThreadLocal.</p>
   * <p>If running multiple tests on same thread, it must be reset after used,
   * since currently it isn't cleared on tests.</p>
   */
  public static void resetMocks() {
    Mocks.reset();
  }

}
