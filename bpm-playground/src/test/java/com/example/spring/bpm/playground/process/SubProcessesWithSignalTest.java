package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
@Slf4j
class SubProcessesWithSignalTest {

  @Test
  @Deployment(resources = "subProcessesWithSignal.bpmn")
  void test_default() {
    var mainTaskInstance = runtimeService().startProcessInstanceByKey("SignalMainTask");
    log.debug("{}", mainTaskInstance);
    assertThat(mainTaskInstance)
        .isActive();

    var task1Instance = runtimeService().startProcessInstanceByKey("SignalTask1");
    log.debug("{}", task1Instance);
    assertThat(task1Instance)
        .isActive()
        .isWaitingAt("ApproveTask1");

    var task2Instance = runtimeService().startProcessInstanceByKey("SignalTask2");
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

  @Test
  @Deployment(resources = "subProcessesWithSignal.bpmn")
  void test_with_manual_signal() {
    var mainTaskInstance = runtimeService().startProcessInstanceByKey("SignalMainTask");
    log.debug("{}", mainTaskInstance);
    assertThat(mainTaskInstance)
        .isActive();

    var task1Instance = runtimeService().startProcessInstanceByKey("SignalTask1");
    log.debug("{}", task1Instance);
    assertThat(task1Instance)
        .isActive()
        .isWaitingAt("ApproveTask1");

    var task2Instance = runtimeService().startProcessInstanceByKey("SignalTask2");
    log.debug("{}", task2Instance);
    assertThat(task2Instance)
        .isActive()
        .isWaitingAt("ApproveTask2");

    complete(task("ApproveTask1", task1Instance));
    assertThat(task1Instance)
        .isEnded();

    runtimeService().createSignalEvent("Task2Signal").send();

    assertThat(mainTaskInstance)
        .isWaitingAt("ValidateTasks");

    complete(task("ValidateTasks", mainTaskInstance));
    assertThat(mainTaskInstance)
        .isEnded();
  }

}