package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategorySpecification;
import org.openpaas.paasta.marketplace.api.repository.CategoryRepository;
import org.springframework.data.domain.Sort;

public class CategoryServiceTest extends AbstractMockTest {

    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        categoryService = new CategoryService(categoryRepository);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getList() {
        CategorySpecification spec = new CategorySpecification();
        Sort sort = Sort.by("id");

        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-02");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        given(categoryRepository.findAll(any(CategorySpecification.class), any(Sort.class))).willReturn(categoryList);

        List<Category> result = categoryService.getList(spec, sort);
        assertEquals(categoryList.size(), result.size());

        verify(categoryRepository).findAll(any(CategorySpecification.class), any(Sort.class));
    }

    @Test
    public void get() {
        Category category1 = category(1L, "category-01");

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category1));

        Category result = categoryService.get(1L);
        assertEquals(category1, result);

        verify(categoryRepository).findById(1L);
    }

    @Test
    public void getByName() {
        Category category1 = category(1L, "category-01");

        given(categoryRepository.findByName(category1.getName())).willReturn(category1);

        Category result = categoryService.getByName(category1.getName());
        assertEquals(category1, result);

        verify(categoryRepository).findByName(category1.getName());
    }

    @Test
    public void create() {
        Category category1 = category(1L, "category-01");

        given(categoryRepository.save(any(Category.class))).willReturn(category1);

        Category result = categoryService.create(category1);
        assertEquals(category1, result);

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void update() {
        Category category1 = category(1L, "category-01");

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category1));

        Category result = categoryService.update(category1);
        assertEquals(category1, result);

        verify(categoryRepository).findById(1L);
    }

    @Test
    public void updateName() {
        Category category1 = category(1L, "category-01");

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category1));

        Category result = categoryService.updateName(category1);
        assertEquals(category1, result);

        verify(categoryRepository).findById(1L);
    }

    @Test
    public void updateSeq() {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-02");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category1));
        given(categoryRepository.findById(2L)).willReturn(Optional.of(category2));
        given(categoryRepository.findAll()).willReturn(categoryList);

        Category result = null;

        result = categoryService.updateSeq(1L, Category.Direction.Down);
        assertTrue(2L == result.getSeq());

        result = categoryService.updateSeq(1L, Category.Direction.Up);
        assertTrue(1L == result.getSeq());

        result = categoryService.updateSeq(2L, Category.Direction.Up);
        assertTrue(1L == result.getSeq());

        result = categoryService.updateSeq(2L, Category.Direction.Down);
        assertTrue(2L == result.getSeq());

        verify(categoryRepository, atLeastOnce()).findById(1L);
        verify(categoryRepository, atLeastOnce()).findAll();
    }

    @Test(expected = RuntimeException.class)
    public void updateSeqWithInvalidUp() {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-02");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category1));
        given(categoryRepository.findAll()).willReturn(categoryList);

        categoryService.updateSeq(1L, Category.Direction.Up);
    }

    @Test(expected = RuntimeException.class)
    public void updateSeqWithInvalidDown() {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-02");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        given(categoryRepository.findById(2L)).willReturn(Optional.of(category2));
        given(categoryRepository.findAll()).willReturn(categoryList);

        categoryService.updateSeq(2L, Category.Direction.Down);
    }

    @Test(expected = RuntimeException.class)
    public void updateSeqWithInvalidId() {
        Category category1 = category(1L, "category-01");

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category1));
        given(categoryRepository.findAll()).willReturn(new ArrayList<Category>());

        categoryService.updateSeq(1L, Category.Direction.Down);
    }

    @Test
    public void delete() {
        categoryService.delete(1L);
    }

}
