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
import ro.mfpc.twopl.model.author.Author;
import ro.mfpc.twopl.model.book.Book;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = {"ro.mfpc.twopl.repository.authorRepo", "ro.mfpc.twopl.repository.bookRepo"},
        entityManagerFactoryRef = "db2EntityManagerFactory",
        transactionManagerRef = "db2TransactionManager"
)
public class DB2Config {

    @Bean
    @ConfigurationProperties(prefix = "app.datasource.db2")
    public DataSourceProperties db2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("app.datasource.db2.configuration")
    public DataSource db2DataSource() {
        return db2DataSourceProperties().initializeDataSourceBuilder()
                .type(BasicDataSource.class).build();
    }

    @Bean(name = "db2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean db2EntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(db2DataSource())
                .packages(Author.class, Book.class)
                .build();
    }

    @Bean
    public PlatformTransactionManager db2TransactionManager(
            final @Qualifier("db2EntityManagerFactory") LocalContainerEntityManagerFactoryBean db2EntityManagerFactory) {
        return new JpaTransactionManager(db2EntityManagerFactory.getObject());
    }
}
