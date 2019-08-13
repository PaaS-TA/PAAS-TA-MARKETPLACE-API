package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategorySpecification;
import org.openpaas.paasta.marketplace.api.service.CategoryService;
import org.springframework.data.domain.Sort;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getList(CategorySpecification spec, Sort sort) {
        return categoryService.getList(spec, sort);
    }

    @GetMapping("/{id}")
    public Category get(@PathVariable Long id) {
        return categoryService.get(id);
    }

    @PostMapping
    public Category create(@NotNull @Validated(Category.Create.class) @RequestBody Category category, BindingResult bindingResult) throws BindException {
        Category sameName = categoryService.getByName(category.getName());
        if (sameName != null) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        return categoryService.create(category);
    }

    @PutMapping("/{id}")
    public Category update(@NotNull @Validated(Category.Update.class) @PathVariable Long id, @RequestBody Category category, BindingResult bindingResult)
            throws BindException {
        Category sameName = categoryService.getByName(category.getName());
        if (sameName != null && id != sameName.getId()) {
            bindingResult.rejectValue("name", "Unique");
        }

        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        category.setId(id);

        return categoryService.update(category);
    }

    @PutMapping("/{id}/name")
    public Category updateName(@NotNull @Validated(Category.UpdateName.class) @PathVariable Long id, @RequestBody Category category) {
        category.setId(id);

        return categoryService.updateName(category);
    }

    @PutMapping("/{id}/{direction}")
    public Category updateSeq(@PathVariable Long id, @PathVariable Category.Direction direction) {
        return categoryService.updateSeq(id, direction);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

}
