package com.example.spring.bpm.playground.decision;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.InputStream;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.test.junit5.DmnEngineExtension;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DmnEngineExtension.class)
class DmnEngineTest {

  @Test
  void simpleOrderDecision(DmnEngine dmnEngine) {
    // Parse decision
    InputStream inputStream = getClass().getResourceAsStream("/decisions/SimpleOrderDecision.dmn");
    DmnDecision decision = dmnEngine.parseDecision("simpleOrderDecision", inputStream);

    // Set input variables
    VariableMap variables = Variables.createVariables()
        .putValue("status", "silver")
        .putValue("sum", 9000);

    // Evaluate decision with id 'orderDecision' from file 'SimpleOrderDecision.dmn'
    DmnDecisionTableResult results = dmnEngine.evaluateDecisionTable(decision, variables);

    // Check that one rule has matched
    assertThat(results).hasSize(1);

    DmnDecisionRuleResult result = results.getSingleResult();
    assertThat(result)
        .containsOnly(
            entry("result", "rejected"),
            entry("reason", "Rejected by status and amount")
        );

    // Change input variables
    variables.putValue("status", "gold");

    // Evaluate decision again
    results = dmnEngine.evaluateDecisionTable(decision, variables);

    // Check new result
    assertThat(results).hasSize(1);

    result = results.getSingleResult();
    assertThat(result)
        .containsOnly(
            entry("result", "accepted"),
            entry("reason", "Accepted by high status")
        );
  }

  @Test
  void variableInExpressionOrderDecision(DmnEngine dmnEngine) {
    // Parse decision
    InputStream inputStream = getClass().getResourceAsStream(
        "/decisions/VariableInExpressionOrderDecision.dmn");
    DmnDecision decision = dmnEngine.parseDecision("variableInExpressionOrderDecision",
        inputStream);

    // Set input variables
    VariableMap variables = Variables.createVariables()
        .putValue("status", "silver")
        .putValue("sum", 9999)
        .putValue("highAmountOrder", 1000);

    // Evaluate decision with id 'orderDecision' from file 'SimpleOrderDecision.dmn'
    DmnDecisionTableResult results = dmnEngine.evaluateDecisionTable(decision, variables);

    // Check that one rule has matched
    assertThat(results).hasSize(1);

    DmnDecisionRuleResult result = results.getSingleResult();
    assertThat(result)
        .containsOnly(
            entry("result", "rejected"),
            entry("reason", "Rejected by OVER 9000! :: 9999")
        );
  }

  @Test
  void advancedExpressionsOrderDecision(DmnEngine dmnEngine) {
    // Parse decision
    InputStream inputStream = getClass().getResourceAsStream(
        "/decisions/AdvancedExpressionsOrderDecision.dmn");
    DmnDecision decision = dmnEngine.parseDecision("advancedExpressionsOrderDecision", inputStream);

    // Set input variables
    VariableMap variables = Variables.createVariables()
        .putValue("status", "silver")
        .putValue("highAmountOrder", 1000);

    // Evaluate decision with id 'orderDecision' from file 'SimpleOrderDecision.dmn'
    DmnDecisionTableResult results = dmnEngine.evaluateDecisionTable(decision, variables);

    // Check that one rule has matched
    assertThat(results).hasSize(1);

    DmnDecisionRuleResult result = results.getSingleResult();
    assertThat(result)
        .containsOnly(
            entry("result", "rejected"),
            entry("reason", "Rejected by OVER 9000! :: 9999")
        );
  }
}
