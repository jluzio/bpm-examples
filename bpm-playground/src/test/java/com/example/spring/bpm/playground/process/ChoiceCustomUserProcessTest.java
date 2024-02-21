package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
@Slf4j
class ChoiceCustomUserProcessTest extends AbstractProcessTest {

  enum Status { APPROVED, REJECTED }

  @Test
  @Deployment(resources = "processes/choice-custom-user.bpmn")
  void approve() {
    var processInstance = runtimeService().startProcessInstanceByKey("ChoiceCustomUserProcess",
        Map.of("approvalUser", "admin"));
    log.debug("{}", processInstance);
    assertThat(processInstance)
        .isActive()
        .hasPassed("StartEvent")
        .isWaitingAt("Approve");

    complete(task(processInstance), Map.of(
        "status", Status.APPROVED.name()
    ));
    assertThat(processInstance)
        .isEnded()
        .hasPassed("AcceptedEvent");
  }

  @Test
  @Deployment(resources = "processes/choice-custom-user.bpmn")
  void reject() {
    var processInstance = runtimeService().startProcessInstanceByKey("ChoiceCustomUserProcess",
        Map.of("approvalUser", "admin"));
    log.debug("{}", processInstance);
    assertThat(processInstance)
        .isActive()
        .hasPassed("StartEvent")
        .isWaitingAt("Approve");

    complete(task(processInstance), Map.of(
        "status", Status.REJECTED.name()
    ));
    assertThat(processInstance)
        .isEnded()
        .hasPassed("RejectedEvent");
  }

}
