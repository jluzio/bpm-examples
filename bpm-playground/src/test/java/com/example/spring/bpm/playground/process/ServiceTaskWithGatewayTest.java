package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.runtimeService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.junit5.ProcessEngineExtension;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Slf4j
@ExtendWith({ProcessEngineExtension.class, MockitoExtension.class})
class ServiceTaskWithGatewayTest extends AbstractProcessTest {

  public enum Result {
    ACCEPTED, REJECTED, ERROR;
  }

  public static class ServiceTask {

    public Result validate(DelegateExecution execution) {
      log.info("execute: {}", execution);
      return Result.ACCEPTED;
    }
  }

  @Spy
  ServiceTask serviceTask = new ServiceTask();

  @BeforeEach
  void setup() {
    Mocks.register("serviceTaskBean", serviceTask);
  }

  @Test
  @Deployment(resources = "processes/service-task-with-gateway.bpmn")
  void test() throws Exception {
    var processInstance = runtimeService().startProcessInstanceByKey("ServiceTaskWithGateway");
    log.debug("{}", processInstance);

    assertThat(processInstance)
        .isEnded()
        .hasPassed("AcceptedEndEvent");
    verify(serviceTask).validate(any());
  }

}
