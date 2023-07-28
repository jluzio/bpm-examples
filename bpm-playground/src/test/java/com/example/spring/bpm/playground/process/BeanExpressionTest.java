package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith({ProcessEngineExtension.class, MockitoExtension.class})
class BeanExpressionTest {

  static class TaskBean implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
      log.info("execute: {}", execution);
    }
  }

  @Spy
  TaskBean taskBean = new TaskBean();

  @BeforeEach
  void setup() {
    Mocks.register("taskBean", taskBean);
  }

  @Test
  @Deployment(resources = "processes/bean-expression.bpmn")
  void test() throws Exception {
    var processInstance = runtimeService().startProcessInstanceByKey("BeanExpression");
    log.debug("{}", processInstance);
    assertThat(processInstance)
        .isEnded();

    verify(taskBean).execute(any());
  }

}
