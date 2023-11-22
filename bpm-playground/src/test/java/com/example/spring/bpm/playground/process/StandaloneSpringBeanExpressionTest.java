package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.example.spring.bpm.playground.test.TestTags;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

@SpringBootTest
@ExtendWith(ProcessEngineExtension.class)
@Slf4j
@Tag(TestTags.NON_ISOLATED_TEST)
class StandaloneSpringBeanExpressionTest {

  @Configuration
  @Import(TaskBean.class)
  static class Config {

    @Autowired
    TaskBean taskBean;

    @PostConstruct
    void init() {
      Mocks.register("taskBean", taskBean);
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
