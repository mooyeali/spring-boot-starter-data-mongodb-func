package cn.com.mooyea.mongofunc.core;

import cn.com.mooyea.mongofunc.utils.ReflectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 支持链式调用的 Lambda 查询构建器（增强版）。
 *
 * @param <T> 实体类型
 */
public class LambdaQuery<T> {

    private final Class<T> entityClass;
    private final List<Criteria> criteriaList = new ArrayList<>();
    private Query query = new Query();

    public LambdaQuery(Class<T> entityClass) {
        Assert.notNull(entityClass, "entityClass 不能为空");
        this.entityClass = entityClass;
    }

    // === 等值 ===
    public <R> LambdaQuery<T> eq(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).is(value));
        return this;
    }

    // === 不等 ===
    public <R> LambdaQuery<T> ne(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).ne(value));
        return this;
    }

    // === in ===
    public <R> LambdaQuery<T> in(ReflectionUtils.SerializableFunction<T, R> fn, Collection<R> values) {
        if (values == null || values.isEmpty()) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).in(values));
        return this;
    }

    // === notIn ===
    public <R> LambdaQuery<T> notIn(ReflectionUtils.SerializableFunction<T, R> fn, Collection<R> values) {
        if (values == null || values.isEmpty()) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).nin(values));
        return this;
    }

    // === like（模糊匹配，使用正则）===
    public LambdaQuery<T> like(ReflectionUtils.SerializableFunction<T, String> fn, String pattern) {
        if (pattern == null || pattern.isEmpty()) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        // 转义特殊正则字符，除非用户明确要正则
        String escaped = Pattern.quote(pattern);
        Criteria criteria = Criteria.where(fieldName).regex(".*" + escaped + ".*", "i"); // i: ignore case
        criteriaList.add(criteria);
        return this;
    }

    // === between（闭区间）===
    public <R extends Comparable<R>> LambdaQuery<T> between(
            ReflectionUtils.SerializableFunction<T, R> fn, R start, R end) {
        if (start == null || end == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).gte(start).lte(end));
        return this;
    }

    // === gt / ge / lt / le ===
    public <R extends Comparable<R>> LambdaQuery<T> gt(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).gt(value));
        return this;
    }

    public <R extends Comparable<R>> LambdaQuery<T> ge(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).gte(value));
        return this;
    }

    public <R extends Comparable<R>> LambdaQuery<T> lt(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).lt(value));
        return this;
    }

    public <R extends Comparable<R>> LambdaQuery<T> le(ReflectionUtils.SerializableFunction<T, R> fn, R value) {
        if (value == null) return this;
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).lte(value));
        return this;
    }

    // === isNull / isNotNull ===
    public LambdaQuery<T> isNull(ReflectionUtils.SerializableFunction<T, ?> fn) {
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).is(null));
        return this;
    }

    public LambdaQuery<T> isNotNull(ReflectionUtils.SerializableFunction<T, ?> fn) {
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        criteriaList.add(Criteria.where(fieldName).ne(null));
        return this;
    }

    // === 排序 ===
    public LambdaQuery<T> orderByAsc(ReflectionUtils.SerializableFunction<T, ?> fn) {
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, fieldName));
        return this;
    }

    public LambdaQuery<T> orderByDesc(ReflectionUtils.SerializableFunction<T, ?> fn) {
        String fieldName = ReflectionUtils.getFieldName(fn, entityClass);
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, fieldName));
        return this;
    }

    // === 构建 Query ===
    public Query build() {
        if (!criteriaList.isEmpty()) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
            query.addCriteria(criteria);
        }
        return query;
    }
}
