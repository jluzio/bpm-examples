package com.example.spring.bpm.playground.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;

public class TestBpmConfig {

  public static ProcessEngine processEngine(Map<Object, Object> beans) {
    StandaloneInMemProcessEngineConfiguration config = (StandaloneInMemProcessEngineConfiguration) ProcessEngineConfiguration
        .createStandaloneInMemProcessEngineConfiguration();
    config.setBeans(beans);
    return config
        .setJdbcUrl("jdbc:h2:mem:camunda;DB_CLOSE_DELAY=1000")
        .buildProcessEngine();
  }

  public static Map<Object, Object> copyToMutableMap(Map<Object, Object> beans) {
    return Optional.ofNullable(beans)
        .map(HashMap::new)
        .orElse(null);
  }

}
