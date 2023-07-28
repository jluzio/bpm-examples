package com.example.spring.bpm.playground.process.onboarding;

import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.REQUIRES_AML_HITS;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.VALID;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.REQUIRES_VALIDATE_ONBOARDING;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component("assistedOnboardingProcessService")
@RequiredArgsConstructor
@Slf4j
public class AssistedOnboardingProcessService {

  public boolean validateProcess(DelegateExecution execution) {
    log.info("validateProcess :: {}", execution);
    return getValue(execution, VALID, true);
  }

  public void ingestCustomer(DelegateExecution execution) {
    log.info("ingestCustomer :: {}", execution);
  }

  public boolean requiresAmlHits(DelegateExecution execution) {
    log.info("requiresAmlHits :: {}", execution);
    return getValue(execution, REQUIRES_AML_HITS, true);
  }

  public boolean requiresValidateOnboarding(DelegateExecution execution) {
    log.info("requiresValidateOnboarding :: {}", execution);
    return getValue(execution, REQUIRES_VALIDATE_ONBOARDING, true);
  }

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

  public void processRejectedEvent(DelegateExecution execution) {
    log.info("processRejectedEvent: {}", execution);
  }

  public void processErrorEvent(DelegateExecution execution) {
    log.info("processErrorEvent: {}", execution);
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
