package com.example.spring.bpm.playground.process;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Slf4j
public class MessageSender implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    log.info("execute: {}", execution);
    var runtimeService = execution.getProcessEngine().getRuntimeService();
    log.info("runtimeService: {}", runtimeService);
//    runtimeService.createMessageCorrelation()
  }

}
