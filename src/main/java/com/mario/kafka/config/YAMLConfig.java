package com.mario.kafka.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class YAMLConfig {

	private String kfktopic;

	public String getKfktopic() {
		return kfktopic;
	}

	public void setKfktopic(String kfktopic) {
		this.kfktopic = kfktopic;
	}

}
