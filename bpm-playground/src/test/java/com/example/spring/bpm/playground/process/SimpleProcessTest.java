package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
@Slf4j
class SimpleProcessTest {

  @Test
  @Deployment(resources = "simple.bpmn")
  void test() {
    var instance = runtimeService().startProcessInstanceByKey("SimpleProcess");
    log.debug("{}", instance);
    assertThat(instance)
        .isActive()
        .hasPassed("StartEvent");
  }

}
