package com.openrangelabs.services.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;

@Configuration
@Profile("!integration")
public class BonitaDBConfig {
    public static final String BONITA_DATASOURCE = "bonita";
    public static final String BONITA_NAMEDJDBCTEMPLATE = "bonita_namedjdbctemplate";

    @Value("org.postgresql.Driver")
    private String driver;

    @Value("${bonitaDBURL}")
    private String url;

    @Value("${bonitaUser}")
    private String username;

    @Value("${bonitaPassword}")
    private String password;

    @Bean(name = BONITA_DATASOURCE)
    DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPassword(password);
        hikariConfig.setUsername(username);
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setConnectionTimeout(15000);
        hikariConfig.setMaximumPoolSize(1);
        hikariConfig.setMinimumIdle(1);
        hikariConfig.setLeakDetectionThreshold(30000);
        hikariConfig.setAllowPoolSuspension(true);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setPoolName("ml-bonita");
        hikariConfig.setConnectionInitSql("SET application_name = 'ML Bonita-BDM'");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        return new TransactionAwareDataSourceProxy(dataSource);
    }

    @Bean(name = BONITA_NAMEDJDBCTEMPLATE)
    public NamedParameterJdbcTemplate bonitaNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }
}
