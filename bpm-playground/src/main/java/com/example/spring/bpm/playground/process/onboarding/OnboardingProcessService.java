package com.example.spring.bpm.playground.process.onboarding;

import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.REQUIRES_AML_HITS;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.REQUIRES_VALIDATE_ONBOARDING;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.VALID;
import static java.util.Optional.ofNullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

@Component("onboardingProcessService")
@RequiredArgsConstructor
@Slf4j
public class OnboardingProcessService {

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

  public void validateOnboardingApprove(DelegateExecution execution) {
    log.info("validateOnboardingApprove :: {}", execution);
  }

  public void validateOnboardingReject(DelegateExecution execution) {
    log.info("validateOnboardingReject :: {}", execution);
  }

  public void amlHitsApprove(DelegateExecution execution) {
    log.info("amlHitsApprove :: {}", execution);
  }

  public void amlHitsReject(DelegateExecution execution) {
    log.info("amlHitsReject :: {}", execution);
  }

  public void amlHitsRedFlaggedReject(DelegateExecution execution) {
    log.info("amlHitsRedFlaggedReject :: {}", execution);
  }

  public void processRejectedEvent(DelegateExecution execution) {
    log.info("processRejectedEvent: {}", execution);
  }

  public void processErrorEvent(DelegateExecution execution) {
    log.info("processErrorEvent: {}", execution);
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
