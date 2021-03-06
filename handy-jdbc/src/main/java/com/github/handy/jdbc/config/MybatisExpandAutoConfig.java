package com.github.handy.jdbc.config;

import com.github.handy.core.constant.Constant;
import com.github.handy.jdbc.mybatis.GenericXmlMapperBuilder;
import com.github.handy.jdbc.mybatis.KeyGenerationMode;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * <p>mybatis 扩展配置，这里重写SqlSessionFactoryBean的初始化,
 *  初始化时扫描mapper文件，并生成通用的增删改查</p>
 *
 * @author rui.zhou
 * @date 2018/12/4 17:53
 */

@Slf4j
@Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
//@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MybatisExpandAutoConfig {

    private final MybatisProperties properties;

    private final Interceptor[] interceptors;

    private final ResourceLoader resourceLoader;

    private final DatabaseIdProvider databaseIdProvider;

    private final List<ConfigurationCustomizer> configurationCustomizers;

    @Value("${mybatis.keyGenerationMode:IDENTITY}")
    private String keyGenerationMode;



    public MybatisExpandAutoConfig(MybatisProperties properties,
                                   ObjectProvider<Interceptor[]> interceptorsProvider,
                                   ResourceLoader resourceLoader,
                                   ObjectProvider<DatabaseIdProvider> databaseIdProvider,
                                   ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
        log.info("MybatisExpandAutoConfig | {}", "MybatisExpandAutoConfig");
        this.properties = properties;
        this.interceptors = interceptorsProvider.getIfAvailable();
        this.resourceLoader = resourceLoader;
        this.databaseIdProvider = databaseIdProvider.getIfAvailable();
        this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
    }

    @Bean
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource")  DataSource dataSource) throws Exception {
        log.info("MybatisExpandAutoConfig | {}", "sqlSessionFactory");
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setVfs(SpringBootVFS.class);
        if (StringUtils.hasText(this.properties.getConfigLocation())) {
            factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
        }
        org.apache.ibatis.session.Configuration configuration = this.properties.getConfiguration();
        if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
            configuration = new org.apache.ibatis.session.Configuration();
        }
        if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
            for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
                customizer.customize(configuration);
            }
        }
        factory.setConfiguration(configuration);
        if (this.properties.getConfigurationProperties() != null) {
            factory.setConfigurationProperties(this.properties.getConfigurationProperties());
        }
        if (!ObjectUtils.isEmpty(this.interceptors)) {
            factory.setPlugins(this.interceptors);
        }
        if (this.databaseIdProvider != null) {
            factory.setDatabaseIdProvider(this.databaseIdProvider);
        }
        if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
            factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
        }
        if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
            factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
        }

        if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
            GenericXmlMapperBuilder builder = new GenericXmlMapperBuilder();
            builder.setBaseResultMap(Constant.BASE_RESULT_MAP);
            builder.setBaseTableName(Constant.BASE_TABLE);
            builder.setBaseColumns(Constant.BASE_COLUMNS);
            builder.setIdGenerationSql(Constant.ID_GENERATION_SQL);
            builder.setKeyGenerationMode(KeyGenerationMode.parse(keyGenerationMode));
            factory.setMapperLocations(builder.builderGenericXmlMapper(this.properties.resolveMapperLocations()));
        }
        return factory.getObject();
    }

    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource hikariDataSource() {
        return new HikariDataSource();
    }

}
