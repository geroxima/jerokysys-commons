package com.jerokysys.commons.service;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.jerokysys.commons.common.PageResult;
import com.jerokysys.commons.common.PagedFilter;
import com.jerokysys.commons.domain.base.BaseEntity;
import com.jerokysys.commons.exception.BusinessException;
import com.jerokysys.commons.repository.BaseRepository;

import lombok.Getter;

@Getter
public abstract class AbstractBaseService<E extends BaseEntity, D, F extends PagedFilter> implements BaseService<E, D, F> {

    protected final BaseRepository<E, Long> repository;
    protected final Class<E> entityClass;
    protected final Class<D> dtoClass;

    @SuppressWarnings("unchecked")
    public AbstractBaseService(BaseRepository<E, Long> repository) {
        this.repository = repository;
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<E>) type.getActualTypeArguments()[0];
        this.dtoClass = (Class<D>) type.getActualTypeArguments()[1];
    }

    @Override
    public D getById(Long id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("ENTITY_NOT_FOUND", "Entity not found with id: " + id));
        return toDto(entity);
    }

    @Override
    public List<D> getAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<D> getByFilter(F filter) {
        return search(filter);
    }

    @Override
    public void delete(Long id) {
        E entity = repository.findById(id)
                .orElseThrow(() -> new BusinessException("ENTITY_NOT_FOUND", "Entity not found with id: " + id));
        if (entity instanceof com.jerokysys.commons.domain.base.BaseEntity) {
            ((com.jerokysys.commons.domain.base.BaseEntity) entity).setDeleted(true);
            ((com.jerokysys.commons.domain.base.BaseEntity) entity).setActive(false);
        }
        repository.save(entity);
    }

    protected Pageable buildPageable(F filter) {
        Sort sort = Sort.by(Sort.Direction.fromString(filter.getDirection()), filter.getSort());
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

    public PageResult<D> toPageResult(Page<D> pageDto) {
        return PageResult.<D>builder()
                .content(pageDto.getContent())
                .totalElements(pageDto.getTotalElements())
                .totalPages(pageDto.getTotalPages())
                .page(pageDto.getNumber())
                .size(pageDto.getSize())
                .build();
    }

    public Page<D> toPageDto(Page<E> page) {
        return page.map(this::toDto);
    }
}
