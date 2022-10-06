package com.astralbrands.upc.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfiguration {

	@Value("${bit.database.url}")
	private String bitDB;

	@Value("${x3.database.url}")
	private String x3DB;

	/*
		Bean for a datasource object that's configured
		to connect to the bitBoot SQL database server |||
		@Primary - Higher Precedence bean in Spring Container
	 */
	@Bean(name="bitBootDataSource")
	@Primary
	public DataSource dataSource() {
		return DataSourceBuilder.create().url(bitDB).build();
		
	}
	/*
    	Bean for a datasource object that's configured
   		to connect to the X3 SQL database server
	 */
	@Bean(name="x3DataSource")
	public DataSource x3DataSource() {
		return DataSourceBuilder.create().url(x3DB).build();
	}
}
