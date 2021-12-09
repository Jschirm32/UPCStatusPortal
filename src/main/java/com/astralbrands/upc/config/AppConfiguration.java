package com.astralbrands.upc.config;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfiguration {

	@Bean(name="bitBootDataSource")
	@Primary
	public DataSource dataSource() {
		return DataSourceBuilder.create().url("jdbc:sqlserver://AB-SAGEDB-01\\X3:1433;DatabaseName=bitBoot;user=bitBoot;password=pluJVT8IEGG").build();
		
	}
	
	@Bean(name="x3DataSource")
	public DataSource x3DataSource() {
		return DataSourceBuilder.create().url("jdbc:sqlserver://AB-SAGEDB-01\\X3:1433;DatabaseName=x3;user=bitBoot;password=pluJVT8IEGG").build();
	}
}
