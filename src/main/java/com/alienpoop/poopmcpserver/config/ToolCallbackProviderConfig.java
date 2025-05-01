package com.alienpoop.poopmcpserver.config;

import com.alienpoop.poopmcpserver.annotation.ToolServer;
import java.util.Map;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolCallbackProviderConfig {

  @Bean
  public ToolCallbackProvider weatherTools(ApplicationContext applicationContext) {
    Map<String, Object> beansWithAnnotation =
        applicationContext.getBeansWithAnnotation(ToolServer.class);

    return MethodToolCallbackProvider.builder()
        .toolObjects(beansWithAnnotation.values().toArray())
        .build();
  }
}
