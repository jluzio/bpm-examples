package com.example.spring.bpm.playground.process.onboarding;

import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.AML_HITS_ENABLED;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.VALID;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.VALIDATE_ONBOARDING_ENABLED;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.util.Map;
import java.util.Optional;
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
//    setDefaultValueIfAbsent(execution, VALID, true);
    return getValue(execution, VALID, true);
  }

  public void ingestCustomer(DelegateExecution execution) {
    log.info("ingestCustomer :: {}", execution);
  }

  public boolean isAmlHitsEnabled(DelegateExecution execution) {
    log.info("isAmlHitsEnabled :: {}", execution);
//    setDefaultValueIfAbsent(execution, AML_HITS_ENABLED, true);
    return getValue(execution, AML_HITS_ENABLED, true);
  }

  public boolean isValidateOnboardingEnabled(DelegateExecution execution) {
    log.info("isValidateOnboardingEnabled :: {}", execution);
//    setDefaultValueIfAbsent(execution, VALIDATE_ONBOARDING_ENABLED, true);
    return getValue(execution, VALIDATE_ONBOARDING_ENABLED, true);
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
