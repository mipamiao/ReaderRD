package com.mipa.websocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Slf4j
@Configuration
public class WSConfig {


	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		System.out.println("ServerEndpointExporter Bean 被实例化了！");
		return new ServerEndpointExporter();
	}

}
