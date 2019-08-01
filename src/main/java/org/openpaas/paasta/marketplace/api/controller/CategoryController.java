package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategorySpecification;
import org.openpaas.paasta.marketplace.api.service.CategoryService;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getList(CategorySpecification spec, Sort sort) {
        return categoryService.getList(spec, sort);
    }

    @GetMapping("/{id}")
    public Category get(@PathVariable Long id) {
        return categoryService.get(id);
    }

}
