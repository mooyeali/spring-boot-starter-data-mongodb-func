package cn.com.mooyea.mongofunc.utils;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.StringUtils;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具类，用于从方法引用（如 Entity::getId）中提取 MongoDB 字段名，
 * 并支持 @Field 注解自定义字段名。
 */
public class ReflectionUtils {

    // 缓存：避免重复反射
    private static final ConcurrentHashMap<String, String> FIELD_NAME_CACHE = new ConcurrentHashMap<>();

    /**
     * 从方法引用中提取 MongoDB 字段名（支持 @Field 注解）
     */
    public static <T> String getFieldName(SerializableFunction<T, ?> fn, Class<T> entityClass) {
        try {
            Method writeReplace = fn.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(fn);
            String implMethodName = lambda.getImplMethodName();

            // 推导 Java 字段名（如 getId → id）
            String javaFieldName;
            if (implMethodName.startsWith("get")) {
                javaFieldName = Character.toLowerCase(implMethodName.charAt(3)) + implMethodName.substring(4);
            } else if (implMethodName.startsWith("is")) {
                javaFieldName = Character.toLowerCase(implMethodName.charAt(2)) + implMethodName.substring(3);
            } else {
                javaFieldName = implMethodName;
            }

            // 缓存键：entityClass.getName() + "#" + javaFieldName
            String cacheKey = entityClass.getName() + "#" + javaFieldName;
            return FIELD_NAME_CACHE.computeIfAbsent(cacheKey, k -> {
                try {
                    java.lang.reflect.Field field = entityClass.getDeclaredField(javaFieldName);
                    Field fieldAnno = field.getAnnotation(Field.class);
                    if (fieldAnno != null && StringUtils.hasText(fieldAnno.value())) {
                        return fieldAnno.value();
                    } else {
                        return javaFieldName;
                    }
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("无法找到字段: " + javaFieldName + " in " + entityClass, e);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException("无法解析字段名，请确保使用标准 getter 方法引用", e);
        }
    }

    /**
     * 函数式接口，用于传递方法引用。
     */
    @FunctionalInterface
    public interface SerializableFunction<T, R> extends java.util.function.Function<T, R>, java.io.Serializable {
    }
}
