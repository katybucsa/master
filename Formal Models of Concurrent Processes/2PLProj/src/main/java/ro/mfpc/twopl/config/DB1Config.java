package ro.mfpc.twopl.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import ro.mfpc.twopl.model.student.Student;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "ro.mfpc.twopl.repository.studentRepo",
        entityManagerFactoryRef = "db1EntityManagerFactory",
        transactionManagerRef = "db1TransactionManager"
)
public class DB1Config {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "app.datasource.db1")
    public DataSourceProperties db1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.db1.configuration")
    public DataSource db1DataSource() {
        return db1DataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

    @Bean(name = "db1EntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean db1EntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(db1DataSource())
                .packages(Student.class)
                .build();
    }

    @Bean
    public PlatformTransactionManager db1TransactionManager(
            final @Qualifier("db1EntityManagerFactory") LocalContainerEntityManagerFactoryBean db1EntityManagerFactory) {
        return new JpaTransactionManager(db1EntityManagerFactory.getObject());
    }
}


