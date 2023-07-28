package com.example.spring.bpm.playground.process.onboarding;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component("onboardingHelperProcessService")
@RequiredArgsConstructor
@Slf4j
public class OnboardingHelperProcessService {

  public void sendMessage(DelegateExecution execution, String messageName) {
    log.info("sendMessage :: {}", execution);
    var businessKey = execution.getBusinessKey();
    log.info("{}", Map.of(
        "messageName", messageName,
        "businessKey", businessKey,
        "variablesLocal", execution.getVariablesLocal()));
    var runtimeService = execution.getProcessEngineServices().getRuntimeService();
    runtimeService.createMessageCorrelation(messageName)
        .processInstanceBusinessKey(businessKey)
        .setVariablesLocal(execution.getVariablesLocal())
        .setVariables(execution.getVariablesLocal())
        .correlate();
  }

  private void setDefaultValueIfAbsent(DelegateExecution execution, String variableName,
      Object defaultValue) {
    Object currentValue = execution.getVariable(variableName);
    if (isNull(currentValue)) {
      execution.setVariable(variableName, defaultValue);
    }
    log.info("setDefaultValueIfAbsent[{}] :: currentValue={} | defaultValue={}",
        variableName, currentValue, defaultValue);
  }

  private <T> T getValue(DelegateExecution execution, String variableName,
      T defaultValue) {
    @SuppressWarnings("unchecked")
    T currentValue = (T) execution.getVariable(variableName);
    log.info("getValue[{}] :: currentValue={} | defaultValue={}",
        variableName, currentValue, defaultValue);
    return ofNullable(currentValue).orElse(defaultValue);
  }

}
