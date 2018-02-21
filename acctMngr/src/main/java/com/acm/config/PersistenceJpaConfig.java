package com.acm.config;

import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.acm.data.repo")
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class PersistenceJpaConfig {
	
	@Resource
	private Environment env;
	
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) {
    	LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
    	emfb.setDataSource(ds);
    	
    	JpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
    	emfb.setJpaVendorAdapter(adapter);
    	emfb.setJpaProperties(additionalProperties());
    	
    	//scan packages with domain classes
    	emfb.setPackagesToScan(env.getRequiredProperty("entitymanager.packages.to.scan"));
    	return emfb;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
       JpaTransactionManager transactionManager = new JpaTransactionManager();
       transactionManager.setEntityManagerFactory(emf);
  
       return transactionManager;
    }
    
    /*
     * Translate persistence exceptions to Spring's extensive list of exceptions.
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation(){
    	return new PersistenceExceptionTranslationPostProcessor();
    }
    
    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", env.getRequiredProperty("spring.jpa.hibernate.ddl-auto"));
        properties.setProperty("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        return properties;
    }
	
}
