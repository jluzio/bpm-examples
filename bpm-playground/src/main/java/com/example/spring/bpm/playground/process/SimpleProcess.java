package com.example.spring.bpm.playground.process;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Component
//@Qualifier("simpleProcess")
@Slf4j
public class SimpleProcess implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    log.info("execute: {}", execution);
  }

}
