package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Component;

@SpringBootTest(classes = {
    CamundaBpmAutoConfiguration.class,
    SpringBeanExpressionTest.TaskBean.class
})
@AutoConfigureDataJdbc
//@AutoConfigureDataJpa
@Slf4j
class SpringBeanExpressionTest {

  @Component("taskBean")
  static class TaskBean implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
      log.info("execute: {}", execution);
    }
  }

  @SpyBean
  TaskBean taskBean;

  @Test
  void test() throws Exception {
    var processInstance = runtimeService().startProcessInstanceByKey("BeanExpression");
    log.debug("{}", processInstance);
    assertThat(processInstance)
        .isEnded();

    verify(taskBean).execute(any());
  }

}
