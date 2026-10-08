package com.jerokysys.commons.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.jerokysys.commons.common.PageResult;
import com.jerokysys.commons.common.PagedFilter;

public interface BaseService<E, D, F extends PagedFilter> {
    D create(D dto);

    D update(Long id, D dto);

    D getById(Long id);

    List<D> getAll();

    PageResult<D> search(F filter);

    PageResult<D> getByFilter(F filter);

    void delete(Long id);

    E toEntity(D dto);

    D toDto(E entity);

    Page<D> toPageDto(Page<E> page);
}
