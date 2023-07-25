package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskQuery;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
@Slf4j
class SubProcessesWithMessageTest {

  @Test
  @Deployment(resources = "subProcessesWithMessage.bpmn")
  void test() {
    var mainTaskInstance = runtimeService().startProcessInstanceByKey("MainTask");
    log.debug("{}", mainTaskInstance);
    assertThat(mainTaskInstance)
        .isActive()
        .isWaitingFor("Task1Message", "Task2Message");

    var task1Instance = runtimeService().startProcessInstanceByKey("Task1");
    log.debug("{}", task1Instance);
    assertThat(task1Instance)
        .isActive()
        .isWaitingAt("ApproveTask1");

    var task2Instance = runtimeService().startProcessInstanceByKey("Task2");
    log.debug("{}", task2Instance);
    assertThat(task2Instance)
        .isActive()
        .isWaitingAt("ApproveTask2");

    complete(task("ApproveTask1", task1Instance));
    assertThat(task1Instance)
        .isEnded();

    complete(task("ApproveTask2", task2Instance));
    assertThat(task2Instance)
        .isEnded();

    assertThat(mainTaskInstance)
        .isWaitingAt("ValidateTasks");

    complete(task("ValidateTasks", mainTaskInstance));
    assertThat(mainTaskInstance)
        .isEnded();
  }

}
