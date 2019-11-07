package org.openpaas.paasta.marketplace.api.service;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.openpaas.paasta.marketplace.api.domain.Category;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractMockTest {

    protected LocalDateTime current;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        current = LocalDateTime.now();
    }

    protected Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName("category-01");
        category.setDescription("descritpion");
        category.setCreatedBy("admin");
        category.setCreatedDate(current);
        category.setLastModifiedBy("admin");
        category.setLastModifiedDate(current);
        category.setSeq(id);
    
        return category;
    }

}
