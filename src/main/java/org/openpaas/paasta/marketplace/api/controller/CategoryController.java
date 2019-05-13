package org.openpaas.paasta.marketplace.api.controller;

import java.util.List;

import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategorySpecification;
import org.openpaas.paasta.marketplace.api.domain.Category.Direction;
import org.openpaas.paasta.marketplace.api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/prototype/categories")
public class CategoryController extends AbstractController {

	@Autowired
    CategoryService categoryService;

    @GetMapping
    public List<Category> getCategoryList(CategorySpecification spec) {
        return categoryService.getCategoryList(spec);
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        logger.info("category={}", category);

        return categoryService.createCategory(category);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);

        return categoryService.updateCategory(category);
    }

    @PutMapping("/{id}/name")
    public Category updateCategoryName(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);

        return categoryService.updateCategoryName(category);
    }

    @PutMapping("/{id}/up")
    public Category updateCategorySeqUp(@PathVariable Long id) {
        return categoryService.updateCategorySeq(id, Direction.Up);
    }

    @PutMapping("/{id}/down")
    public Category updateCategorySeqDown(@PathVariable Long id) {
        return categoryService.updateCategorySeq(id, Direction.Down);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }

}
