package com.example.spring.bpm.playground.process.onboarding;

import static com.example.spring.bpm.playground.process.onboarding.ProcessData.EventName.AML_HITS_RESULT_RECEIVED_EVENT;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.EventName.DATA_COMPLETE_EVENT;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.EventName.REJECTED_ERROR;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.AGGREGATE_RESULT;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.FORM_KEY;
import static com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableName.STATUS;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.example.spring.bpm.playground.process.onboarding.ProcessData.ProcessId;
import com.example.spring.bpm.playground.process.onboarding.ProcessData.VariableValue;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.spring.boot.starter.CamundaBpmAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(classes = {
    CamundaBpmAutoConfiguration.class,
    OnboardingTest.Config.class
})
@AutoConfigureDataJpa
@Slf4j
class OnboardingTest {

  @Configuration
  @Import({AssistedOnboardingProcessService.class})
  static class Config {

  }

  @SpyBean
  AssistedOnboardingProcessService processService;
  @Autowired
  RuntimeService runtimeService;

  @Test
  void success_valid_customer() throws Exception {
    String businessKey = UUID.randomUUID().toString();
    var processInstance = runtimeService.startProcessInstanceByKey(
        ProcessId.ONBOARDING,
        businessKey,
        Map.of(FORM_KEY, "")
    );
    log.debug("{}", processInstance);

    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT, AML_HITS_RESULT_RECEIVED_EVENT);
    verify(processService).requiresAmlHits(any());

    runtimeService.createMessageCorrelation(AML_HITS_RESULT_RECEIVED_EVENT)
        .processInstanceBusinessKey(businessKey)
        .setVariables(Map.of(
            AGGREGATE_RESULT, VariableValue.AGGREGATE_RESULT_CLEAN
        ))
        .correlate();
    assertThat(processInstance)
        .isWaitingFor(DATA_COMPLETE_EVENT)
        .hasPassed("AmlHitsCustomerApprovedEndEvent");

    runtimeService.createMessageCorrelation(DATA_COMPLETE_EVENT)
        .processInstanceBusinessKey(businessKey)
        .setVariables(Map.of(
            "validateOnboardingUser", "admin"
        ))
        .correlate();
    assertThat(processInstance)
        .isWaitingAt("ValidateOnboardingCustomerServiceAgentApproval");
    verify(processService).requiresValidateOnboarding(any());

    complete(task(), Map.of(
        STATUS, VariableValue.STATUS_APPROVED
    ));
    assertThat(processInstance)
        .hasPassed("ValidateOnboardingCustomerApprovedEndEvent");

    assertThat(processInstance)
        .hasPassed("ValidateProcess", "IngestUser", "OnboardingDoneEndEvent")
        .isEnded();
  }


  @Test
  void error_rejected_event() throws Exception {
    doThrow(new BpmnError("RejectedError", "Rejected Error"))
        .when(processService).requiresAmlHits(any());

    String businessKey = UUID.randomUUID().toString();
    var processInstance = runtimeService.startProcessInstanceByKey(
        ProcessId.ONBOARDING,
        businessKey,
        Map.of(FORM_KEY, "")
    );
    log.debug("{}", processInstance);

//    assertThat(processInstance)
//        .isWaitingFor(DATA_COMPLETE_EVENT, AML_HITS_RESULT_RECEIVED_EVENT);
//    verify(processService).requiresAmlHits(any());

    assertThat(processInstance)
        .hasPassed("RejectedEndEvent")
        .isEnded();
    verify(processService).processRejectedEvent(any());
  }

}
