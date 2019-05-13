package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategorySpecification;
import org.openpaas.paasta.marketplace.api.domain.Category.Direction;
import org.openpaas.paasta.marketplace.api.exception.NotFoundException;
import org.openpaas.paasta.marketplace.api.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@Transactional
public class CategoryService extends AbstractService {

	@Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getCategoryList(CategorySpecification spec) {
        return categoryRepository.findAll(spec);
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Category createCategory(Category category) {
        category = categoryRepository.save(category);
        category.setSeq(category.getId());

        arrangeCategorySeq();

        return category;
    }

    public Category updateCategory(Category category) {
        Assert.notNull(category, "category can't be null.");
        Assert.notNull(category.getId(), "category id can't be null.");

        Category saved = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new NotFoundException("saved category can't be null."));

        saved.setName(category.getName());
//        saved.setUseYn(category.getUseYn());

        return categoryRepository.save(saved);
    }

    public Category updateCategoryName(Category category) {
        Assert.notNull(category, "category can't be null.");
        Assert.notNull(category.getId(), "category id can't be null.");

        Category saved = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new NotFoundException("saved category can't be null."));

        saved.setName(category.getName());

        return categoryRepository.save(saved);
    }

    public Category updateCategorySeq(Long id, Direction direction) {
        Assert.notNull(id, "id can't be null.");

        Category saved = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("saved category can't be null."));

        swapCategorySeq(saved, direction);
        arrangeCategorySeq();

        return saved;
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);

        arrangeCategorySeq();
    }

    private void swapCategorySeq(Category category, Direction direction) {
        List<Category> categoryList = categoryRepository.findAll();
        categoryList.sort((Category c0, Category c1) -> (int) (c0.getSeq() - c1.getSeq()));
        int index = -1;
        for (int i = 0; i < categoryList.size(); i++) {
            Category c = categoryList.get(i);
            if (c == category) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new RuntimeException("can't find the category's index.");
        }
        int targetIndex = -1;
        if (direction == Direction.Up) {
            targetIndex = index - 1;
        }
        if (direction == Direction.Down) {
            targetIndex = index + 1;
        }
        if (targetIndex < 0 || targetIndex > categoryList.size() - 1) {
            throw new RuntimeException("can't find the category's target for swap seq. target index: " + targetIndex);
        }

        Category target = categoryList.get(targetIndex);

        logger.info("swap: {}, {}", category, target);

        Long temp = category.getSeq();
        category.setSeq(target.getSeq());
        target.setSeq(temp);
    }

    private void arrangeCategorySeq() {
        List<Category> categoryList = categoryRepository.findAll();
        categoryList.sort((Category c0, Category c1) -> (int) (c0.getSeq() - c1.getSeq()));
        int i = 0;
        for (Category category : categoryList) {
            category.setSeq((long) ++i);
        }
    }

}
