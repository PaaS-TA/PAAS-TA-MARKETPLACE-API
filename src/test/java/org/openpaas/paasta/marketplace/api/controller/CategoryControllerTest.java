package org.openpaas.paasta.marketplace.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.CategorySpecification;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.openpaas.paasta.marketplace.api.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringRunner.class)
@WebMvcTest(CategoryController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CategoryService categoryService;

    String userId;
    String adminId;
    LocalDateTime current;

    @Before
    public void setUp() throws Exception {
        userId = "foo";
        adminId = "admin";
        current = LocalDateTime.now();
    }

    @After
    public void tearDown() throws Exception {
    }

    private Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName("category-01");
        category.setCreatedBy("admin");
        category.setCreatedDate(current);
        category.setLastModifiedBy("admin");
        category.setLastModifiedDate(current);
        category.setSeq(id);

        return category;
    }

    @Test
    public void getList() throws Exception {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-02");

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category1);
        categoryList.add(category2);

        given(categoryService.getList(any(CategorySpecification.class), any(Sort.class))).willReturn(categoryList);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/categories")
                .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/category/list",
                preprocessRequest(
                        modifyUris()
                            .scheme("http")
                            .host("marketplace.yourdomain.com")
                            .removePort(),
                        prettyPrint()
                    ),
                preprocessResponse(
                        prettyPrint()
                    ),
                pathParameters(
                    ),
                requestParameters(
                    ),
                relaxedResponseFields(
                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("list of categories"),
                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("[].name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("[].seq").type(JsonFieldType.NUMBER).description("order in category list")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void get() throws Exception {
        Category category = category(1L, "category-01");

        given(categoryService.get(eq(1L))).willReturn(category);

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/categories/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/category/get",
                preprocessRequest(
                        modifyUris()
                            .scheme("http")
                            .host("marketplace.yourdomain.com")
                            .removePort(),
                        prettyPrint()
                    ),
                preprocessResponse(
                        prettyPrint()
                    ),
                pathParameters(
                        parameterWithName("id").description("Software's id")
                    ),
                requestParameters(
                    ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("seq").type(JsonFieldType.NUMBER).description("order in category list")
                    )
                )
            );
        // @formatter:on
    }

}
