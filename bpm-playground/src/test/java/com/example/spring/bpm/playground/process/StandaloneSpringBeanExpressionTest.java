package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@Slf4j
class StandaloneSpringBeanExpressionTest {

  static Map<Object, Object> beans = new HashMap<>();

  @RegisterExtension
  ProcessEngineExtension extension = ProcessEngineExtension
      .builder()
      .useProcessEngine(TestBpmConfig.processEngine(beans))
      .build();

  @Configuration
  @Import(TaskBean.class)
  static class Config {

    @Autowired
    TaskBean taskBean;

    @PostConstruct
    void init() {
      beans.put("taskBean", taskBean);
    }
  }

  @Component
  static class TaskBean implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
      log.info("execute: {}", execution);
    }
  }

  @SpyBean
  TaskBean taskBean;

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
