package com.example.spring.bpm.playground.process.multiple;

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
class SubProcessesTest {

  @Test
  @Deployment(resources = "processes/multiple/sub-processes.bpmn")
  void success() {
    var processInstance = runtimeService().startProcessInstanceByKey("SubProcesses", "test");
    log.debug("{}", processInstance);
    assertThat(processInstance)
        .isActive()
        .hasPassed("StartEvent");

    List<Task> tasks = taskQuery()
        .processInstanceId(processInstance.getProcessInstanceId())
        .list();
    log.debug("tasks: {}", tasks);
    Assertions.assertThat(tasks)
        .hasSize(2)
        .map(Task::getTaskDefinitionKey)
        .contains("ApproveTask1", "ApproveTask2");

    tasks.forEach(BpmnAwareTests::complete);

    assertThat(processInstance)
        .task()
        .hasDefinitionKey("ValidateTasks");

    complete(task());

    assertThat(processInstance)
        .isEnded()
        .hasPassed("SuccessEndEvent");
  }

  @Test
  @Deployment(resources = "processes/multiple/sub-processes.bpmn")
  void error() {
    var processInstance = runtimeService().startProcessInstanceByKey("SubProcesses", "error");
    log.debug("{}", processInstance);
    assertThat(processInstance)
        .isActive()
        .hasPassed("StartEvent")
        .isWaitingAt("ReportError");

    complete(task());

    assertThat(processInstance)
        .isEnded()
        .hasPassed("ErrorEndEvent");
  }

}
