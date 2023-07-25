package com.example.spring.bpm.playground.process;

import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.Expression;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@Data
@Slf4j
public class MessageSender implements JavaDelegate {

  private Expression messageName;

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    log.info("execute: {}", execution);

    var runtimeService = execution.getProcessEngine().getRuntimeService();
    var processBusinessKey = execution.getProcessBusinessKey();
    var resolvedMessageName = messageName.getValue(execution).toString();

    log.info("data: {}", Map.of(
        "runtimeService", runtimeService,
        "processBusinessKey", processBusinessKey,
        "resolvedMessageName", resolvedMessageName
    ));

    runtimeService.createMessageCorrelation(resolvedMessageName)
        .processInstanceBusinessKey(processBusinessKey)
        .correlate();
  }

}
