package io.github.mooyeali.mongofunc.service;

import io.github.mooyeali.mongofunc.core.LambdaDelete;
import io.github.mooyeali.mongofunc.core.LambdaQuery;
import io.github.mooyeali.mongofunc.core.LambdaUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 通用 CRUD 服务实现。
 *
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
@Service
public abstract class ServiceImpl<T, ID> implements IService<T, ID> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    /**
     * 子类必须提供实体类类型
     */
    private Class<T> entityClass;

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> getEntityClass() {
        if (entityClass == null) {
            Type genericSuperclass = getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) genericSuperclass;
                entityClass = (Class<T>) pt.getActualTypeArguments()[0];
            } else {
                throw new IllegalStateException("无法推断泛型实体类，请确保 ServiceImpl 被正确继承");
            }
        }
        return entityClass;
    }

    @Override
    public T save(T entity) {
        return mongoTemplate.save(entity);
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        return (List<T>) mongoTemplate.insertAll(entities);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(mongoTemplate.findById(id, getEntityClass()));
    }

    @Override
    public List<T> findAll() {
        return mongoTemplate.findAll(getEntityClass());
    }

    @Override
    public Page<T> page(Pageable pageable) {
        Query query = new Query().with(pageable);
        List<T> list = mongoTemplate.find(query, getEntityClass());
        long total = mongoTemplate.count(new Query(), getEntityClass());
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<T> page(LambdaQuery<T> lambdaQuery, Pageable pageable) {
        Query query = lambdaQuery.build().with(pageable);
        List<T> list = mongoTemplate.find(query, getEntityClass());
        long total = mongoTemplate.count(lambdaQuery.build(), getEntityClass());
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public void deleteById(ID id) {
        mongoTemplate.remove(Objects.requireNonNull(mongoTemplate.findById(id, getEntityClass())));
    }

    @Override
    public void deleteAll(Iterable<T> entities) {
        entities.forEach(mongoTemplate::remove);
    }

    @Override
    public long update(LambdaUpdate<T> lambdaUpdate) {
        Query query = lambdaUpdate.buildQuery();
        Update update = lambdaUpdate.getUpdate();
        // 返回修改的文档数量
        return mongoTemplate.updateMulti(query, update, getEntityClass()).getModifiedCount();
    }

    @Override
    public long delete(LambdaDelete<T> lambdaDelete) {
        Query query = lambdaDelete.build();
        // 注意：MongoTemplate.remove() 返回的是被删除的文档（不是数量）
        // 若需数量，可先 count 再 delete，或使用 writeResult（但 Spring Data 不直接暴露）
        // 此处使用 remove 并估算（实际项目中可考虑用 bulkOps）
        List<T> deleted = mongoTemplate.find(query, getEntityClass());
        mongoTemplate.remove(query, getEntityClass());
        return deleted.size();
    }
}
