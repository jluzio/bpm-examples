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
  }

  @UtilityClass
  public static class VariableName {

    public static final String VALIDATE_ONBOARDING_ENABLED = "validateOnboardingEnabled";
    public static final String AML_HITS_ENABLED = "amlHitsEnabled";
    public static final String STATUS = "status";
    public static final String VALID = "valid";
    public static final String AGGREGATE_RESULT = "aggregateResult";
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
