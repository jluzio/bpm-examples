package com.example.spring.bpm.playground.config;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.spring.boot.starter.event.PostDeployEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@ConditionalOnProperty(value = "app.process-bootstrap", matchIfMissing = true)
public class ProcessBootstrap {

  @Autowired
  private RuntimeService runtimeService;

  @EventListener
  public void processPostDeploy(PostDeployEvent event) {
    runtimeService.startProcessInstanceByKey("SimpleProcess");
  }

}
