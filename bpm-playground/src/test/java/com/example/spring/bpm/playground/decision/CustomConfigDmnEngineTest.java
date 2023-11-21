package com.example.spring.bpm.playground.decision;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.io.InputStream;
import java.util.List;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionRuleResult;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.test.junit5.DmnEngineExtension;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;


class CustomConfigDmnEngineTest {

  OrdersCustomFunctionProvider ordersCustomFunctionProvider = Mockito.spy(
      new OrdersCustomFunctionProvider());
  DmnEngineConfiguration myConfiguration = new DefaultDmnEngineConfiguration()
      .feelCustomFunctionProviders(List.of(ordersCustomFunctionProvider));

  @RegisterExtension
  DmnEngineExtension dmnEngineExtension = DmnEngineExtension.forConfiguration(myConfiguration);

  @Test
  void advancedExpressionsOrderDecision(DmnEngine dmnEngine) {
    // Parse decision
    InputStream inputStream = getClass().getResourceAsStream(
        "/decisions/AdvancedExpressionsOrderDecision.dmn");
    DmnDecision decision = dmnEngine.parseDecision("advancedExpressionsOrderDecision", inputStream);

    // Set input variables
    VariableMap variables = Variables.createVariables()
        .putValue("status", "silver")
        .putValue("sum", 999)
        .putValue("customerNames", List.of("John Doe"));

    // Evaluate decision with id 'orderDecision' from file 'SimpleOrderDecision.dmn'
    DmnDecisionTableResult results = dmnEngine.evaluateDecisionTable(decision, variables);

    // Check that one rule has matched
    assertThat(results).hasSize(1);

    DmnDecisionRuleResult result = results.getSingleResult();
    assertThat(result)
        .containsOnly(
            entry("result", "rejected"),
            entry("reason", "Rejected by status and blocklist")
        );

    Mockito.verify(ordersCustomFunctionProvider)
        .resolveFunction("getHighAmountOrder");
  }
}
