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

//import javax.sql.DataSource;
@Configuration
@Profile("!integration")
public class BloxopsDBConfig {
    public static final String BLOXOPS_DATASOURCE = "bloxops";

    public static final String BLOXOPS_NAMEDJDBCTEMPLATE = "bloxopts_namedjdbctemplate";

    @Value("org.postgresql.Driver")
    private String driver;

    @Value("${bloxopsDBURL}")
    private String url;

    @Value("${bloxopsUser}")
    private String username;

    @Value("${bloxopsPassword}")
    private String password;

    @Bean(name = BLOXOPS_DATASOURCE)
    DataSource dataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPassword(password);
        hikariConfig.setUsername(username);
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setConnectionTimeout(15000);
        hikariConfig.setMaximumPoolSize(3);
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setLeakDetectionThreshold(30000);
        hikariConfig.setAllowPoolSuspension(true);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setPoolName("ml-bloxops");
        hikariConfig.setConnectionInitSql("SET application_name = 'ML Bloxops'");
        hikariConfig.setConnectionTestQuery("SELECT 1");
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        return new TransactionAwareDataSourceProxy(dataSource);
    }

    @Bean(name = BLOXOPS_NAMEDJDBCTEMPLATE)
    public NamedParameterJdbcTemplate bloxOpsNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(dataSource());
    }
}
