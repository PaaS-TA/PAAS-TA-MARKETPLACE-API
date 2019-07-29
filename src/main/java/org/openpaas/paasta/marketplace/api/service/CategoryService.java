package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import javax.transaction.Transactional;

import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategorySpecification;
import org.openpaas.paasta.marketplace.api.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getList(CategorySpecification spec) {
        return categoryRepository.findAll(spec);
    }

    public Category get(Long id) {
        return categoryRepository.findById(id).get();
    }

    public Category create(Category category) {
        category = categoryRepository.save(category);
        category.setSeq(category.getId());

        arrangeSeq();

        return category;
    }

    public Category updateName(Category category) {
        Assert.notNull(category, "category can't be null.");
        Assert.notNull(category.getId(), "category id can't be null.");

        Category saved = categoryRepository.findById(category.getId()).get();

        saved.setName(category.getName());

        return saved;
    }

    public Category updateSeq(Long id, Category.Direction direction) {
        Assert.notNull(id, "id can't be null.");

        Category saved = categoryRepository.findById(id).get();

        swapSeq(saved, direction);
        arrangeSeq();

        return saved;
    }

    public void delete(Long id) {
        categoryRepository.deleteById(id);

        arrangeSeq();
    }

    private void swapSeq(Category category, Category.Direction direction) {
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
        if (direction == Category.Direction.Up) {
            targetIndex = index - 1;
        }
        if (direction == Category.Direction.Down) {
            targetIndex = index + 1;
        }
        if (targetIndex < 0 || targetIndex > categoryList.size() - 1) {
            throw new RuntimeException("can't find the category's target for swap seq. target index: " + targetIndex);
        }

        Category target = categoryList.get(targetIndex);

        log.info("swap: {}, {}", category, target);

        Long temp = category.getSeq();
        category.setSeq(target.getSeq());
        target.setSeq(temp);
    }

    private void arrangeSeq() {
        List<Category> categoryList = categoryRepository.findAll();
        categoryList.sort((Category c0, Category c1) -> (int) (c0.getSeq() - c1.getSeq()));
        int i = 0;
        for (Category category : categoryList) {
            category.setSeq((long) ++i);
        }
    }

}
