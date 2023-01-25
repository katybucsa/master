package ro.mfpc.twopl.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import ro.mfpc.twopl.model.borrow.Borrow;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "ro.mfpc.twopl.repository.borrowRepo",
        entityManagerFactoryRef = "db3EntityManagerFactory",
        transactionManagerRef = "db3TransactionManager"
)
public class DB3Config {

    @Bean
    @ConfigurationProperties(prefix = "app.datasource.db3")
    public DataSourceProperties db3DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.db3.configuration")
    public DataSource db3DataSource() {
        return db3DataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

    @Bean(name = "db3EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean db3EntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(db3DataSource())
                .packages(Borrow.class)
                .build();
    }

    @Bean
    public PlatformTransactionManager db3TransactionManager(
            final @Qualifier("db3EntityManagerFactory") LocalContainerEntityManagerFactoryBean db3EntityManagerFactory) {
        return new JpaTransactionManager(db3EntityManagerFactory.getObject());
    }
}
