package com.jerokysys.commons.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.jerokysys.commons.common.PageResult;
import com.jerokysys.commons.common.PagedFilter;
import com.jerokysys.commons.common.Response;
import com.jerokysys.commons.domain.base.BaseEntity;
import com.jerokysys.commons.service.AbstractBaseService;

import jakarta.validation.Valid;

public abstract class BaseController<E extends BaseEntity, D, F extends PagedFilter, S extends AbstractBaseService<E, D, F>> {

    protected final S service;

    public BaseController(S service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<D> create(@Valid @RequestBody D dto) {
        D created = service.create(dto);
        return Response.created(created, "Created successfully");
    }

    @PutMapping("/{id}")
    public Response<D> update(@PathVariable Long id, @Valid @RequestBody D dto) {
        D updated = service.update(id, dto);
        return Response.success(updated, "Updated successfully");
    }

    @GetMapping("/{id}")
    public Response<D> getById(@PathVariable Long id) {
        D entity = service.getById(id);
        return Response.success(entity);
    }

    @GetMapping("/all")
    public Response<List<D>> getAll() {
        List<D> list = service.getAll();
        return Response.success(list);
    }

    @GetMapping
    public Response<PageResult<D>> getByFilter(@ModelAttribute F filter) {
        PageResult<D> result = service.getByFilter(filter);
        return Response.success(result);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Response<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Response.success("Deleted successfully");
    }
}
