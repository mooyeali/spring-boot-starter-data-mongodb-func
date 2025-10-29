package cn.com.mooyea.mongofunc.core;

import cn.com.mooyea.mongofunc.utils.ReflectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持链式调用的 Lambda 更新构建器。
 * 用于构造 MongoDB 的 Update 对象，配合 MongoTemplate.updateMulti() 等方法使用。
 *
 * @param <T> 实体类型
 */
public class LambdaUpdate<T> {

    private final Class<T> entityClass;
    private final Update update = new Update();
    private final List<Criteria> criteriaList = new ArrayList<>();

    public LambdaUpdate(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 设置字段值（对应 $set）
     */
    public <R> LambdaUpdate<T> set(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) {
            // 可选：是否允许 set null？MongoDB 中 set null 是合法的
            // 此处保留 null，由业务决定
        }
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        update.set(fieldName, value);
        return this;
    }

    /**
     * 增加数值（对应 $inc）
     */
    public LambdaUpdate<T> inc(ReflectionUtils.SerializableFunction<T, Number> fn, Number value) {
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        update.inc(fieldName, value);
        return this;
    }

    /**
     * 添加到数组末尾（$push）
     */
    public <R> LambdaUpdate<T> push(ReflectionUtils.SerializableFunction<T, ?> fn, R value) {
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        update.push(fieldName, value);
        return this;
    }

    /**
     * 从数组中移除所有匹配项（$pull）
     */
    public <R> LambdaUpdate<T> pull(ReflectionUtils.SerializableFunction<T, ?> fn, R value) {
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        update.pull(fieldName, value);
        return this;
    }

    /**
     * 条件：eq（用于 where 子句）
     */
    public <R> LambdaUpdate<T> eq(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).is(value));
        return this;
    }

    /**
     * 构建最终的 Query（用于匹配文档）
     */
    public Query buildQuery() {
        Query query = new Query();
        if (!criteriaList.isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
            query.addCriteria(criteria);
        }
        return query;
    }

    /**
     * 获取 Update 对象（用于 MongoTemplate.update...()）
     */
    public Update getUpdate() {
        return update;
    }
}
