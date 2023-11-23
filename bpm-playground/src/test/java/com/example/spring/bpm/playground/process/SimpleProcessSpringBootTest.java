package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import com.example.spring.bpm.playground.test.TestTags;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
@Tag(TestTags.NON_ISOLATED_TEST)
class SimpleProcessSpringBootTest {

  @Autowired
  RuntimeService runtimeService;

  @Test
  void test() {
    var processInstance = runtimeService.startProcessInstanceByKey("SimpleProcess");
    log.debug("{}", processInstance);
    BpmnAwareTests.assertThat(processInstance)
        .isActive()
        .hasPassed("StartEvent");

    // And it should be the only instance
    Assertions.assertThat(processInstanceQuery().count()).isEqualTo(1);
    // And there should exist just a single task within that process instance
    BpmnAwareTests.assertThat(task(processInstance)).isNotNull();

    // When we complete that task
    complete(task(processInstance));
    // Then the process instance should be ended
    BpmnAwareTests.assertThat(processInstance).isEnded();
  }

}
