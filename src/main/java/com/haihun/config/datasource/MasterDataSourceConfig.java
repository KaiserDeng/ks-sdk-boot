package com.haihun.config.datasource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * @author kaiser·von·d
 * @version 2018/4/20
 */

//　这里使用用的MapperScan是 tk的，如果不使用通用Mapper的话请使用mybatis自带的
@MapperScan(basePackages = {MasterDataSourceConfig.BASE_PACKAGES/*,"com.haihun.comm.base"*/},
        sqlSessionTemplateRef = "masterSqlSessionTemplate",
        sqlSessionFactoryRef = "masterSqlSessionFactory")
@Configuration
public class MasterDataSourceConfig {

    static final String BASE_PACKAGES = "com.haihun.sdk.mapper";
    private static final String TYPE_ALIASES_PACKAGE = "com.haihun.sdk.pojo";
    private static final String MAPPER_LOCATION = "classpath:mybatis/mapper/*.xml";
    private static final String DATASOURCE_PREFIX = "master.datasource";

    @Bean(name = "masterDataSourceProperties")
    @ConfigurationProperties(prefix = DATASOURCE_PREFIX)
    public DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "masterDataSource")
    public DataSource masterDataSource() {
        return masterDataSourceProperties().initializeDataSourceBuilder().build();
    }


    @Bean
    @ConfigurationProperties(prefix = "mybatis.configuration")
    public org.apache.ibatis.session.Configuration mybatisConfiguration() {
        return new org.apache.ibatis.session.Configuration();
    }


    @Bean(name = "masterSqlSessionFactory")
    @Primary
    public SqlSessionFactory masterSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();

        bean.setDataSource(masterDataSource());
        bean.setConfiguration(mybatisConfiguration());
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_LOCATION));
        bean.setTypeAliasesPackage(TYPE_ALIASES_PACKAGE);
        return bean.getObject();
    }

    @Bean(name = "masterTransactionManager")
    @Primary
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(masterDataSource());
    }


    @Primary
    @Bean(name = "masterSqlSessionTemplate")
    public SqlSessionTemplate masterSqlSessionTemplate(@Qualifier("masterSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}