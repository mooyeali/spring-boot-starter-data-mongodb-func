package io.github.mooyeali.mongofunc.service;

import io.github.mooyeali.mongofunc.core.LambdaDelete;
import io.github.mooyeali.mongofunc.core.LambdaQuery;
import io.github.mooyeali.mongofunc.core.LambdaUpdate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 通用 CRUD 服务接口。
 *
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public interface IService<T, ID> {

    /**
     * 保存实体
     */
    T save(T entity);

    /**
     * 批量保存
     */
    List<T> saveAll(List<T> entities);

    /**
     * 根据 ID 查询
     */
    Optional<T> findById(ID id);

    /**
     * 查询所有
     */
    List<T> findAll();

    /**
     * 分页查询
     */
    Page<T> page(Pageable pageable);

    /**
     * 条件分页查询（使用 LambdaQuery）
     */
    Page<T> page(LambdaQuery<T> query, Pageable pageable);

    /**
     * 删除
     */
    void deleteById(ID id);

    /**
     * 批量删除
     */
    void deleteAll(Iterable<T> entities);

    /**
     * 根据条件更新（批量）
     */
    long update(LambdaUpdate<T> update);

    /**
     * 根据条件删除
     */
    long delete(LambdaDelete<T> delete);

    default LambdaQuery<T> lambdaQuery() {
        return new LambdaQuery<>(getEntityClass());
    }

    default LambdaUpdate<T> lambdaUpdate() {
        return new LambdaUpdate<>(getEntityClass());
    }

    default LambdaDelete<T> lambdaDelete() {
        return new LambdaDelete<>(getEntityClass());
    }

    Class<T> getEntityClass();
}
