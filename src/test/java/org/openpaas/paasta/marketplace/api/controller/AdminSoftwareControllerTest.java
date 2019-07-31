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
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedRequestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Software.Status;
import org.openpaas.paasta.marketplace.api.domain.Software.Type;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.Yn;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@WebMvcTest(AdminSoftwareController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class AdminSoftwareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SoftwareService softwareService;

    String userId;
    String adminId;
    LocalDateTime current;

    @Before
    public void setUp() throws Exception {
        userId = "foo";
        adminId = "admin";
        current = LocalDateTime.now();
    }

    private Category category(Long id, String name) {
        Category category = new Category();
        category.setId(1L);
        category.setName("category-01");
        category.setCreatedBy("admin");
        category.setCreatedDate(current);
        category.setLastModifiedBy("admin");
        category.setLastModifiedDate(current);

        return category;
    }

    private Software software(Long id, String name, Category category) {
        Software software = new Software();
        software.setId(1L);
        software.setName(name);
        software.setStatus(Status.Approval);
        software.setCategory(category);
        software.setSummary("category-01's summary.");
        software.setDescription("description of this software. create by " + userId);
        software.setCreatedBy(userId);
        software.setCreatedDate(current);
        software.setLastModifiedBy(userId);
        software.setLastModifiedDate(current);
        software.setApp("app-" + UUID.randomUUID().toString() + ".jar");
        software.setManifest("manifest-" + UUID.randomUUID().toString() + ".yml");
        software.setIcon("icon-" + UUID.randomUUID().toString() + ".png");
        List<String> screenshotList = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            screenshotList.add("screenshot-" + UUID.randomUUID().toString() + ".jpg");
        }
        software.setScreenshotList(screenshotList);
        software.setType(Type.Web);
        software.setPricePerDay(1000L);
        software.setVersion("1.0");
        software.setInUse(Yn.Y);

        return software;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getPage() throws Exception {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Software software2 = software(1L, "software-02", category2);
        software2.setCreatedBy("bar");

        Pageable pageable = PageRequest.of(0, 10);

        List<Software> content = new ArrayList<>();
        content.add(software1);
        content.add(software2);
        Page<Software> page = new PageImpl<>(content, pageable, content.size());

        given(softwareService.getPage(any(SoftwareSpecification.class), any(Pageable.class))).willReturn(page);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/page")
                .param("page", "0").param("size", "10").param("sort", "id,asc").param("categoryId", "1")
                .param("nameLike", "software").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization", adminId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("admin/software/page",
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
                        parameterWithName("page").description("index of page (starting from 0)"),
                        parameterWithName("size").description("size of page"),
                        parameterWithName("sort").description("sort condition (column,direction)"),
                        parameterWithName("categoryId").description("category's id"),
                        parameterWithName("nameLike").description("search word of name")
                    ),
                relaxedResponseFields(
                        fieldWithPath("content").type(JsonFieldType.ARRAY).description("content of page"),
                        fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("request pageable"),
                        fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("total count of elements")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void get() throws Exception {
        Category category = category(1L, "category-01");
        Software software = software(1L, "software-01", category);

        given(softwareService.get(eq(1L))).willReturn(software);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", adminId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("admin/software/get",
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
                        fieldWithPath("status").type(JsonFieldType.STRING).description("status (" + StringUtils.arrayToCommaDelimitedString(Status.values()) +")"),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("app").type(JsonFieldType.STRING).description("app file"),
                        fieldWithPath("manifest").type(JsonFieldType.STRING).description("manifest file"),
                        fieldWithPath("icon").type(JsonFieldType.STRING).description("icon file"),
                        fieldWithPath("screenshotList").type(JsonFieldType.ARRAY).description("screenshot files"),
                        fieldWithPath("summary").type(JsonFieldType.STRING).description("brief description"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("detailed description"),
                        fieldWithPath("pricePerDay").type(JsonFieldType.NUMBER).description("price per day"),
                        fieldWithPath("version").type(JsonFieldType.STRING).description("version")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void updateMetadata() throws Exception {
        Category category = category(2L, "category-02");
        Software software = software(1L, "software-rename-01", category);
        software.setStatus(Status.Approval);
        software.setConfirmComment("confirm");

        Category c = new Category();
        c.setId(category.getId());

        Software s = new Software();
        s.setId(software.getId());
        s.setName(software.getName());
        s.setCategory(software.getCategory());
        s.setInUse(software.getInUse());
        s.setStatus(software.getStatus());
        s.setConfirmComment(software.getConfirmComment());

        given(softwareService.updateMetadata(any(Software.class))).willReturn(software);
        given(softwareService.get(eq(1L))).willReturn(software);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.put("/admin/softwares/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .header("Authorization", adminId).content(objectMapper.writeValueAsString(s))
                .characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("admin/software/update",
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
                relaxedRequestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("inUse").type(JsonFieldType.STRING).description("usage status (" + StringUtils.arrayToCommaDelimitedString(Yn.values()) +")"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("usage status (" + StringUtils.arrayToCommaDelimitedString(Yn.values()) +")"),
                        fieldWithPath("confirmComment").type(JsonFieldType.STRING).description("reason for approval or rejected")
                    ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description("status (" + StringUtils.arrayToCommaDelimitedString(Status.values()) +")"),
                        fieldWithPath("inUse").type(JsonFieldType.STRING).description("usage status (" + StringUtils.arrayToCommaDelimitedString(Yn.values()) +")"),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("app").type(JsonFieldType.STRING).description("app file"),
                        fieldWithPath("manifest").type(JsonFieldType.STRING).description("manifest file"),
                        fieldWithPath("icon").type(JsonFieldType.STRING).description("icon file"),
                        fieldWithPath("screenshotList").type(JsonFieldType.ARRAY).description("screenshot files"),
                        fieldWithPath("summary").type(JsonFieldType.STRING).description("brief description"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("detailed description"),
                        fieldWithPath("pricePerDay").type(JsonFieldType.NUMBER).description("price per day"),
                        fieldWithPath("version").type(JsonFieldType.STRING).description("version")
                    )
                )
            );
        // @formatter:on
    }

}
