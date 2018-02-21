package com.acm.config;


import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class PersistenceDataSourceConfig {
	
	@Resource
	private Environment env;
	
	//@Profile("dev")
	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(env.getRequiredProperty("spring.datasource.driver"));
		ds.setUrl(env.getRequiredProperty("spring.datasource.url"));
		ds.setUsername(env.getRequiredProperty("spring.datasource.username"));
		ds.setPassword(env.getRequiredProperty("spring.datasource.password"));
		return ds;
	}

}
