package com.example.spring.bpm.playground.decision;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.dmn.feel.impl.scala.function.CustomFunction;
import org.camunda.bpm.dmn.feel.impl.scala.function.FeelCustomFunctionProvider;

public class OrdersCustomFunctionProvider implements FeelCustomFunctionProvider {

  private Map<String, CustomFunction> functionMap = Map.of(
      "getHighAmountOrder", buildGetHighAmountOrder());

  private CustomFunction buildGetHighAmountOrder() {
    return CustomFunction.create().setParams("status").setFunction(params -> {
      var status = String.valueOf(params.get(0));
      return switch (status) {
        case "silver" -> 1000;
        case "gold" -> Integer.MAX_VALUE;
        default -> 0;
      };
    }).build();
  }

  @Override
  public Collection<String> getFunctionNames() {
    return functionMap.keySet();
  }

  @Override
  public Optional<CustomFunction> resolveFunction(String functionName) {
    return ofNullable(functionMap.get(functionName));
  }

}