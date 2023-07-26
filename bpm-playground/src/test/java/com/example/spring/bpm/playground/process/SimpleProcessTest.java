package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
@Slf4j
class SimpleProcessTest {

  @Test
  @Deployment(resources = "processes/simple.bpmn")
  void test() {
    var processInstance = runtimeService().startProcessInstanceByKey("SimpleProcess");
    log.debug("{}", processInstance);
    assertThat(processInstance)
        .isActive()
        .hasPassed("StartEvent");

    // And it should be the only instance
    Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1);
    // And there should exist just a single task within that process instance
    assertThat(task(processInstance)).isNotNull();

    // When we complete that task
    complete(task(processInstance));
    // Then the process instance should be ended
    assertThat(processInstance).isEnded();
  }

}
