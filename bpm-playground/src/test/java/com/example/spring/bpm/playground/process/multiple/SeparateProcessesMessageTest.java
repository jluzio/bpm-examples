package com.example.spring.bpm.playground.process.multiple;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;

import com.example.spring.bpm.playground.process.AbstractProcessTest;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ProcessEngineExtension.class)
@Slf4j
class SeparateProcessesMessageTest extends AbstractProcessTest {

  @Test
  @Deployment(resources = "processes/multiple/separate-processes-message.bpmn")
  void test_default() {
    var businessKey = "main-task-1";

    var mainTaskInstance = runtimeService().startProcessInstanceByKey("MessageMainTask", businessKey);
    log.debug("{}", mainTaskInstance);
    assertThat(mainTaskInstance)
        .isActive()
        .isWaitingFor("Task1Message", "Task2Message");

    var task1Instance = runtimeService().startProcessInstanceByKey("MessageTask1", businessKey);
    log.debug("{}", task1Instance);
    assertThat(task1Instance)
        .isActive()
        .isWaitingAt("ApproveTask1");

    var task2Instance = runtimeService().startProcessInstanceByKey("MessageTask2", businessKey);
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
  @Deployment(resources = "processes/multiple/separate-processes-message.bpmn")
  void test_external_messages() {
    var mainTaskInstance = runtimeService().startProcessInstanceByKey("MessageMainTask");
    log.debug("{}", mainTaskInstance);
    assertThat(mainTaskInstance)
        .isActive()
        .isWaitingFor("Task1Message", "Task2Message");

    runtimeService().createMessageCorrelation("Task1Message")
        .processInstanceId(mainTaskInstance.getProcessInstanceId())
        .correlate();
    runtimeService().createMessageCorrelation("Task2Message")
        .processInstanceId(mainTaskInstance.getProcessInstanceId())
        .correlate();

    assertThat(mainTaskInstance)
        .isWaitingAt("ValidateTasks");

    complete(task("ValidateTasks", mainTaskInstance));
    assertThat(mainTaskInstance)
        .isEnded();
  }

}
