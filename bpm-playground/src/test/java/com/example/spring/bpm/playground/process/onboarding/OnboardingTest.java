package com.example.spring.bpm.playground.process.onboarding;

import static com.example.spring.bpm.playground.process.onboarding.ProcessData.EventName.AML_SCREENING_RESULT_RECEIVED_EVENT;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.EventName.DATA_COMPLETE_EVENT;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.EventName.ERROR;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.EventName.REJECTED_ERROR;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.AGGREGATE_RESULT;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.FORM_KEY;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.STATUS;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.VALIDATE_ONBOARDING_USER;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableValue.AGGREGATE_RESULT_CLEAN_EDD;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableValue.AGGREGATE_RESULT_RED_FLAGGED;
import static org.awaitility.Awaitility.await;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.example.spring.bpm.playground.process.onboarding.ProcessData.ProcessId;
import com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableValue;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.AutoConfigureDataJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {
    CamundaBpmAutoConfiguration.class,
    OnboardingTest.Config.class
})
@AutoConfigureDataJdbc
@Slf4j
class OnboardingTest {

  public static final String DEFAULT_USER = "admin";

  @TestConfiguration
  @Import({OnboardingProcessService.class})
  static class Config {

  }

  @SpyBean
  OnboardingProcessService processService;
  @Autowired
  RuntimeService runtimeService;

  @Test
  void success_valid_customer() {
    String businessKey = createBusinessKey();
    var processInstance = createProcessInstance(businessKey);
    log.debug("process[{}] :: {}", businessKey, processInstance);

    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT, AML_SCREENING_RESULT_RECEIVED_EVENT);
    verify(processService).requiresAmlScreening(any());
    verify(processService).amlScreeningCallScreeningApi(any());

    sendAmlScreeningResultReceivedEvent(businessKey, VariableValue.AGGREGATE_RESULT_CLEAN);
    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT)
        .hasPassed("AmlScreeningCustomerApprovedEndEvent");
    verify(processService).amlScreeningApprove(any());

    sendDataCompleteEvent(businessKey);
    assertThat(processInstance)
        .isWaitingAt("ValidateOnboardingCustomerServiceAgentApproval");
    verify(processService).requiresValidateOnboarding(any());

    waitForTask(processInstance);
    complete(task(), Map.of(
        STATUS, VariableValue.STATUS_APPROVED
    ));
    assertThat(processInstance)
        .hasPassed("ValidateOnboardingCustomerApprovedEndEvent");
    verify(processService).validateOnboardingApprove(any());

    assertThat(processInstance)
        .hasPassed("ValidateProcess", "IngestUser", "OnboardingDoneEndEvent")
        .isEnded();
    verify(processService).validateProcess(any());
    verify(processService).ingestCustomer(any());
  }

  @Test
  void error_rejected_event() {
    doReturn(false)
        .when(processService).requiresAmlScreening(any());
    doReturn(false)
        .when(processService).requiresValidateOnboarding(any());
    doThrow(new BpmnError(REJECTED_ERROR, "Rejected Error"))
        .when(processService).validateProcess(any());

    String businessKey = createBusinessKey();
    var processInstance = createProcessInstance(businessKey);
    log.debug("process[{}] :: {}", businessKey, processInstance);

    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT);
    verify(processService, never()).amlScreeningCallScreeningApi(any());

    sendDataCompleteEvent(businessKey);

    assertThat(processInstance)
        .hasPassed("RejectedEndEvent")
        .isEnded();
    verify(processService).processRejectedEvent(any());
    verify(processService).amlScreeningApprove(any());
    verify(processService).validateOnboardingApprove(any());
    verify(processService).validateProcess(any());
  }

  @Test
  void error_event() {
    doReturn(false)
        .when(processService).requiresAmlScreening(any());
    doReturn(false)
        .when(processService).requiresValidateOnboarding(any());
    doThrow(new BpmnError(ERROR, "Some Error"))
        .when(processService).validateProcess(any());

    String businessKey = createBusinessKey();
    var processInstance = createProcessInstance(businessKey);
    log.debug("process[{}] :: {}", businessKey, processInstance);

    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT);
    verify(processService, never()).amlScreeningCallScreeningApi(any());

    sendDataCompleteEvent(businessKey);

    assertThat(processInstance)
        .hasPassed("ErrorEndEvent")
        .isEnded();
    verify(processService).processErrorEvent(any());
    verify(processService).amlScreeningApprove(any());
    verify(processService).validateOnboardingApprove(any());
  }

  @Test
  void fail_validate_onboarding_reject() {
    doReturn(false)
        .when(processService).requiresAmlScreening(any());
    doReturn(false)
        .when(processService).validateProcess(any());

    String businessKey = createBusinessKey();
    var processInstance = createProcessInstance(businessKey);
    log.debug("process[{}] :: {}", businessKey, processInstance);

    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT);
    verify(processService).amlScreeningApprove(any());
    verify(processService, never()).amlScreeningCallScreeningApi(any());

    sendDataCompleteEvent(businessKey);
    assertThat(processInstance)
        .isWaitingAt("ValidateOnboardingCustomerServiceAgentApproval");
    verify(processService).requiresValidateOnboarding(any());

    waitForTask(processInstance);
    complete(task(), Map.of(
        STATUS, VariableValue.STATUS_REJECTED
    ));
    assertThat(processInstance)
        .hasPassed("ValidateOnboardingCustomerRejectedEndEvent");
    verify(processService).validateOnboardingReject(any());

    assertThat(processInstance)
        .hasPassed("ValidateProcess", "OnboardingCustomerRejectedEndEvent")
        .isEnded();
  }

  @Test
  void fail_aml_hits_reject() {
    doReturn(false)
        .when(processService).requiresValidateOnboarding(any());
    doReturn(false)
        .when(processService).validateProcess(any());

    String businessKey = createBusinessKey();
    var processInstance = createProcessInstance(businessKey);
    log.debug("process[{}] :: {}", businessKey, processInstance);

    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT, AML_SCREENING_RESULT_RECEIVED_EVENT);
    verify(processService).amlScreeningCallScreeningApi(any());

    sendDataCompleteEvent(businessKey);
    verify(processService).validateOnboardingApprove(any());

    sendAmlScreeningResultReceivedEvent(businessKey, AGGREGATE_RESULT_CLEAN_EDD);
    assertThat(processInstance)
        .isWaitingAt("AmlScreeningCustomerServiceAgentApproval");

    waitForTask(processInstance);
    complete(task(), Map.of(
        STATUS, VariableValue.STATUS_REJECTED
    ));
    assertThat(processInstance)
        .hasPassed("AmlScreeningCustomerRejectedEndEvent");
    verify(processService).amlScreeningReject(any());

    assertThat(processInstance)
        .hasPassed("ValidateProcess", "OnboardingCustomerRejectedEndEvent")
        .isEnded();
  }

  @Test
  void fail_aml_hits_red_flagged() {
    doReturn(false)
        .when(processService).requiresValidateOnboarding(any());
    doReturn(false)
        .when(processService).validateProcess(any());

    String businessKey = createBusinessKey();
    var processInstance = createProcessInstance(businessKey);
    log.debug("process[{}] :: {}", businessKey, processInstance);

    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT, AML_SCREENING_RESULT_RECEIVED_EVENT);
    verify(processService).amlScreeningCallScreeningApi(any());

    sendDataCompleteEvent(businessKey);
    verify(processService).validateOnboardingApprove(any());

    sendAmlScreeningResultReceivedEvent(businessKey, AGGREGATE_RESULT_RED_FLAGGED);
    assertThat(processInstance)
        .hasPassed("AmlScreeningCustomerRedFlaggedEndEvent");
    verify(processService).amlScreeningRedFlaggedReject(any());

    assertThat(processInstance)
        .hasPassed("ValidateProcess", "OnboardingCustomerRejectedEndEvent")
        .isEnded();
  }

  private ProcessInstance createProcessInstance(String businessKey) {
    return runtimeService.startProcessInstanceByKey(
        ProcessId.ONBOARDING,
        businessKey,
        Map.of(FORM_KEY, "")
    );
  }

  private void sendAmlScreeningResultReceivedEvent(String businessKey, String value) {
    runtimeService.createMessageCorrelation(AML_SCREENING_RESULT_RECEIVED_EVENT)
        .processInstanceBusinessKey(businessKey)
        .setVariables(Map.of(
            AGGREGATE_RESULT, value
        ))
        .correlate();
  }

  private void sendDataCompleteEvent(String businessKey) {
    runtimeService.createMessageCorrelation(DATA_COMPLETE_EVENT)
        .processInstanceBusinessKey(businessKey)
        .setVariables(Map.of(
            VALIDATE_ONBOARDING_USER, DEFAULT_USER
        ))
        .correlate();
  }

  //  static AtomicInteger counter = new AtomicInteger(0);
  private String createBusinessKey() {
    return UUID.randomUUID().toString();
//    return String.valueOf(counter.getAndIncrement());
  }

  /**
   * Validates that the task was created and ready
   * <p>TODO: fix issue with asserts</p>
   * <p>TODO: fix issue with <code>task() == null</code> when running all class tests (this doesn't
   * happen when running only one test)</p>
   * <p>NOTE: as workaround: used await (different thread) to wait for it to be ready</p>
   *
   * @param processInstance
   */
  private void waitForTask(ProcessInstance processInstance) {
    await()
        .until(() -> task(processInstance) != null);
    assertThat(task()).isNotNull();
  }

}
