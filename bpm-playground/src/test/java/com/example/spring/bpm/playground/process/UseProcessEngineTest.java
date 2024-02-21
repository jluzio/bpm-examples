package com.example.spring.bpm.playground.process;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.complete;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.init;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processInstanceQuery;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.task;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class UseProcessEngineTest extends AbstractProcessTest {

  private ProcessEngine usedProcessEngine = ProcessEngineConfiguration
      .createStandaloneInMemProcessEngineConfiguration()
      .setJdbcUrl("jdbc:h2:mem:camunda_custom;DB_CLOSE_ON_EXIT=TRUE")
      .buildProcessEngine();

  @RegisterExtension
  ProcessEngineExtension extension = ProcessEngineExtension
      .builder()
      .useProcessEngine(usedProcessEngine)
      .build();

  @BeforeEach
  public void setup() {
    init(usedProcessEngine);
  }

  @Test
  @Deployment(resources = "processes/testProcess.bpmn")
  void testHappyPath() {
    // Given we create a new process instance
    ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("testProcess");
    // Then it should be active
    assertThat(processInstance).isActive();
    // And it should be the only instance
    assertThat(processInstanceQuery().count()).isEqualTo(1);
    // And there should exist just a single task within that process instance
    assertThat(task(processInstance)).isNotNull();

    // When we complete that task
    complete(task(processInstance));
    // Then the process instance should be ended
    assertThat(processInstance).isEnded();
  }

}