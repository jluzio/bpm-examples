package com.example.spring.bpm.playground.process.onboarding;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ProcessData {

  @UtilityClass
  public static class ProcessId {

    public static final String ONBOARDING = "onboarding";
  }

  @UtilityClass
  public class EventName {

    public static final String AML_HITS_RESULT_RECEIVED_EVENT = "AmlHitsResultReceivedEvent";
    public static final String DATA_COMPLETE_EVENT = "DataCompleteEvent";
    public static final String REJECTED_ERROR = "RejectedError";
    public static final String ERROR = "Error";
  }

  @UtilityClass
  public static class VariableName {

    public static final String REQUIRES_VALIDATE_ONBOARDING = "requiresValidateOnboarding";
    public static final String REQUIRES_AML_HITS = "requiresAmlHits";
    public static final String STATUS = "status";
    public static final String VALID = "valid";
    public static final String AGGREGATE_RESULT = "aggregateResult";
    public static final String FORM_KEY = "formKey";
  }

  @UtilityClass
  public static class VariableValue {

    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String AGGREGATE_RESULT_CLEAN = "CLEAN";
    public static final String AGGREGATE_CLEAN_EDD = "CLEAN_EDD";
    public static final String AGGREGATE_RESULT_RED_FLAGGED = "REDFLAGGED";
  }

}
