package cn.com.mooyea.mongofunc.annotation;

import cn.com.mooyea.mongofunc.autoconfigure.MongoFunctionalAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 MongoDB 函数式 CRUD 框架的注解。
 * 使用此注解将自动导入 MongoFunctionalAutoConfiguration。
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MongoFunctionalAutoConfiguration.class)
public @interface EnableMongoFunctional {
}
