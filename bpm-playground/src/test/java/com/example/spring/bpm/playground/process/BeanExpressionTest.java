package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

@Slf4j
class BeanExpressionTest {

  static TaskBean taskBean = Mockito.spy(new TaskBean());

  static ProcessEngine processEngine() {
    StandaloneInMemProcessEngineConfiguration config = (StandaloneInMemProcessEngineConfiguration) ProcessEngineConfiguration
        .createStandaloneInMemProcessEngineConfiguration();
    config.setBeans(new HashMap<>(Map.of("taskBean", taskBean)));
    return config
        .setJdbcUrl("jdbc:h2:mem:camunda;DB_CLOSE_DELAY=1000")
        .buildProcessEngine();
  };

  @RegisterExtension
  ProcessEngineExtension extension = ProcessEngineExtension
      .builder()
      .useProcessEngine(processEngine())
      .build();

  static class TaskBean implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
      log.info("execute: {}", execution);
    }
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
