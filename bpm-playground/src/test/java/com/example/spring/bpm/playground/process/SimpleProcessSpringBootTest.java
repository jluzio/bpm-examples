package com.example.spring.bpm.playground.process;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "process-bootstrap=false")
@Slf4j
class SimpleProcessSpringBootTest {

  @Autowired
  RuntimeService runtimeService;

  @Test
  void test() {
    var instance = runtimeService.startProcessInstanceByKey("SimpleProcess");
    log.debug("{}", instance);
    assertThat(instance)
        .isActive()
        .hasPassed("StartEvent");
  }

}
