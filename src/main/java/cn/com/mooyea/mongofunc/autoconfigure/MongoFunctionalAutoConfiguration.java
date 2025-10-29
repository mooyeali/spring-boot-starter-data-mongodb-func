package cn.com.mooyea.mongofunc.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;


/**
 * 自动配置类，当存在 MongoTemplate 时自动启用本框架。
 */
@Configuration
@ConditionalOnClass(MongoTemplate.class)
@ComponentScan(basePackages = "cn.com.mooyea.mongofunc")
public class MongoFunctionalAutoConfiguration {
}
