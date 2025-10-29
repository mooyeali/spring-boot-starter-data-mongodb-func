package io.github.mooyeali.mongofunc.core;

import io.github.mooyeali.mongofunc.utils.ReflectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持链式调用的 Lambda 删除构建器。
 * 用于构造删除条件 Query。
 *
 * @param <T> 实体类型
 */
public class LambdaDelete<T> {

    private final Class<T> entityClass;
    private final List<Criteria> criteriaList = new ArrayList<>();

    public LambdaDelete(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 等值条件
     */
    public <R> LambdaDelete<T> eq(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).is(value));
        return this;
    }

    /**
     * 构建最终的 Query 对象，用于删除操作
     */
    public Query build() {
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
            query.addCriteria(criteria);
        }
        return query;
    }
}
