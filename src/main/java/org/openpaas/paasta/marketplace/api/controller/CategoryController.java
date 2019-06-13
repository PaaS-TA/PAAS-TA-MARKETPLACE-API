package org.openpaas.paasta.marketplace.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.openpaas.paasta.marketplace.api.common.ApiConstants;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategoryList;
import org.openpaas.paasta.marketplace.api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = ApiConstants.URI_API_CATEGORY)
@Slf4j
public class CategoryController extends AbstractController {

	@Autowired
    private CategoryService categoryService;

    /**
     * 카테고리 목록 조회
     *
     * @return CategoryList
     */
    @GetMapping
    public CategoryList getCategoryList() {
        return categoryService.getCategoryList();
    }

    /**
     * 카테고리 상세조회
     * 
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    /**
     * 카테고리 등록
     * 
     * @param category
     * @return
     */
    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        log.info("category={}", category);

        return categoryService.createCategory(category);
    }

    /**
     * 카테고리 수정
     * 
     * @param id
     * @param category
     * @return
     */
    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return categoryService.updateCategory(id, category);
    }

//    @PutMapping("/{id}/name")
//    public Category updateCategoryName(@PathVariable Long id, @RequestBody Category category) {
//        category.setId(id);
//
//        return categoryService.updateCategoryName(category);
//    }

//    @DeleteMapping("/{id}")
//    public void deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//    }

}
