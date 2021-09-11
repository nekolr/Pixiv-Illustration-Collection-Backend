package dev.cheerfun.pixivic.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @author OysterQAQ
 * @version 1.0
 * @date 2020/11/4 6:51 PM
 * @description SecondaryDataSourceConfig
 */
@Configuration
@MapperScan(basePackages = "**.secmapper", sqlSessionTemplateRef = "SecondarySessionTemplate")
public class SecondaryDataSourceConfig {
    @Bean(name = "SecondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource SecondaryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "SecondarySessionFactory")
    public SqlSessionFactory SecondarySessionFactory(@Qualifier("SecondaryDataSource") DataSource dataSource, MybatisProperties mybatisProperties) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setConfiguration(mybatisProperties.getConfiguration());
        return bean.getObject();
    }

    @Bean(name = "SecondaryTransactionManager")
    public DataSourceTransactionManager SecondaryTransactionManager(@Qualifier("SecondaryDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "SecondarySessionTemplate")
    public SqlSessionTemplate SecondarySessionTemplate(@Qualifier("SecondarySessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
