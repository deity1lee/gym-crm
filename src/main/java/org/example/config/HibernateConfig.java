package org.example.config;

import org.example.utilities.PersistenceProperties;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    private final PersistenceProperties persistenceProperties;

    public HibernateConfig(PersistenceProperties jdbcProperties) {
        this.persistenceProperties = jdbcProperties;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setDriverClassName(persistenceProperties.getDriver());
        ds.setUrl(persistenceProperties.getUrl());
        ds.setUsername(persistenceProperties.getUsername());
        ds.setPassword(persistenceProperties.getPassword());
        return ds;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource());
        factory.setPackagesToScan("org.example.entities");
        factory.setHibernateProperties(hibernateProperties());
        return factory;
    }

    private Properties hibernateProperties() {
        Properties props = new Properties();
        props.put("hibernate.hbm2ddl.auto", persistenceProperties.getDdlAuto());
        props.put("hibernate.show_sql", persistenceProperties.isShowSql());
        props.put("hibernate.dialect", persistenceProperties.getDialect());
        return props;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }
}
