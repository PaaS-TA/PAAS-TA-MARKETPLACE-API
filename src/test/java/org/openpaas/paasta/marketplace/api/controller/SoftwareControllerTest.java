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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.Software;
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
@WebMvcTest(SoftwareController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class SoftwareControllerTest {

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

    @After
    public void tearDown() throws Exception {
    }

    private Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setCreatedBy("admin");
        category.setCreatedDate(current);
        category.setLastModifiedBy("admin");
        category.setLastModifiedDate(current);
        category.setSeq(id);

        return category;
    }

    private Software software(Long id, String name, Category category) {
        Software software = new Software();
        software.setId(1L);
        software.setName(name);
        software.setStatus(Software.Status.Approval);
        software.setCategory(category);
        software.setSummary("category-01's summary.");
        software.setDescription("description of this software. create by " + userId);
        software.setCreatedBy(userId);
        software.setCreatedDate(current);
        software.setLastModifiedBy(userId);
        software.setLastModifiedDate(current);
        software.setApp(String.format("app-%s.jar", UUID.randomUUID()));
        software.setManifest(String.format("manifest-%s.yml", UUID.randomUUID()));
        software.setIcon(String.format("icon-%s.png", UUID.randomUUID()));
        List<String> screenshotList = new ArrayList<>();
        for (long i = 1; i <= 3; i++) {
            screenshotList.add(String.format("screenshot-%s.jpg", UUID.randomUUID()));
        }
        software.setScreenshotList(screenshotList);
        software.setType(Software.Type.Web);
        software.setPricePerMonth(1000L);
        software.setVersion("1.0");
        software.setInUse(Yn.Y);

        return software;
    }

    @Test
    public void getPage() throws Exception {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-02");
        Software software1 = software(1L, "software-01", category1);
        Software software2 = software(2L, "software-02", category2);
        software2.setCreatedBy("bar");

        Pageable pageable = PageRequest.of(0, 10);

        List<Software> content = new ArrayList<>();
        content.add(software1);
        content.add(software2);
        Page<Software> page = new PageImpl<>(content, pageable, content.size());

        given(softwareService.getPage(any(SoftwareSpecification.class), any(Pageable.class))).willReturn(page);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/softwares/page")
                .param("page", "0").param("size", "10").param("sort", "id,asc").param("categoryId", "1")
                .param("nameLike", "software").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/software/page",
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
    public void getMyPage() throws Exception {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Software software2 = software(2L, "software-02", category2);

        LocalDate currentDate = current.toLocalDate();
        LocalTime midnight = LocalTime.of(0, 0);
        LocalDateTime dateTimeAfter = LocalDateTime.of(currentDate, midnight);
        LocalDateTime dateTimeBefore = dateTimeAfter.plusDays(1);
        String createdDateAfter = dateTimeAfter.format(DateTimeFormatter.ISO_DATE_TIME);
        String createdDateBefore = dateTimeBefore.format(DateTimeFormatter.ISO_DATE_TIME);
        String statusModifiedDateAfter = dateTimeAfter.format(DateTimeFormatter.ISO_DATE_TIME);
        String statusModifiedDateBefore = dateTimeBefore.format(DateTimeFormatter.ISO_DATE_TIME);

        Pageable pageable = PageRequest.of(0, 10);

        List<Software> content = new ArrayList<>();
        content.add(software1);
        content.add(software2);
        Page<Software> page = new PageImpl<>(content, pageable, content.size());

        given(softwareService.getPage(any(SoftwareSpecification.class), any(Pageable.class))).willReturn(page);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/softwares/my/page")
                .param("page", "0").param("size", "10").param("sort", "id,asc").param("categoryId", "1")
                .param("nameLike", "software").contentType(MediaType.APPLICATION_JSON)
                .param("createdDateAfter", createdDateAfter).param("createdDateBefore", createdDateBefore)
                .param("statusModifiedDateAfter", statusModifiedDateAfter).param("statusModifiedDateBefore", statusModifiedDateBefore)
                .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/software/my-page",
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
                        parameterWithName("nameLike").description("search word of name"),
                        parameterWithName("createdDateAfter").description("start created date time"),
                        parameterWithName("createdDateBefore").description("end created date time"),
                        parameterWithName("statusModifiedDateAfter").description("start status modified date time"),
                        parameterWithName("statusModifiedDateBefore").description("end status modified date time")
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

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/softwares/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/software/get",
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
                        parameterWithName("id").description("Category's id")
                    ),
                requestParameters(
                    ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(String.format("status (%s)", StringUtils.arrayToCommaDelimitedString(Software.Status.values()))),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("app").type(JsonFieldType.STRING).description("app file"),
                        fieldWithPath("manifest").type(JsonFieldType.STRING).description("manifest file"),
                        fieldWithPath("icon").type(JsonFieldType.STRING).description("icon file"),
                        fieldWithPath("screenshotList").type(JsonFieldType.ARRAY).description("screenshot files"),
                        fieldWithPath("summary").type(JsonFieldType.STRING).description("brief description"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("detailed description"),
                        fieldWithPath("pricePerMonth").type(JsonFieldType.NUMBER).description("price per month"),
                        fieldWithPath("version").type(JsonFieldType.STRING).description("version")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void create() throws Exception {
        Category category = category(1L, "category-01");
        Software software = software(1L, "software-01", category);
        software.setStatus(Software.Status.Pending);

        Category c = new Category();
        c.setId(category.getId());

        Software s = new Software();
        s.setName(software.getName());
        s.setCategory(c);
        s.setApp(software.getApp());
        s.setManifest(software.getManifest());
        s.setIcon(software.getIcon());
        s.setScreenshotList(software.getScreenshotList());
        s.setSummary(software.getSummary());
        s.setDescription(software.getDescription());
        s.setType(software.getType());
        s.setPricePerMonth(software.getPricePerMonth());
        s.setVersion(software.getVersion());
        s.setInUse(software.getInUse());

        given(softwareService.create(any(Software.class))).willReturn(software);

        ResultActions result = this.mockMvc
                .perform(RestDocumentationRequestBuilders.post("/softwares").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId)
                        .content(objectMapper.writeValueAsString(s)).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/software/create",
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
                relaxedRequestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("summary").type(JsonFieldType.STRING).description("brief description"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("detailed description"),
                        fieldWithPath("inUse").type(JsonFieldType.STRING).description(String.format("usage status (%s)", StringUtils.arrayToCommaDelimitedString(Yn.values())))
                    ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(String.format("status (%s)", StringUtils.arrayToCommaDelimitedString(Software.Status.values()))),
                        fieldWithPath("inUse").type(JsonFieldType.STRING).description(String.format("usage status (%s)", StringUtils.arrayToCommaDelimitedString(Yn.values()))),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("app").type(JsonFieldType.STRING).description("app file"),
                        fieldWithPath("manifest").type(JsonFieldType.STRING).description("manifest file"),
                        fieldWithPath("icon").type(JsonFieldType.STRING).description("icon file"),
                        fieldWithPath("screenshotList").type(JsonFieldType.ARRAY).description("screenshot files"),
                        fieldWithPath("summary").type(JsonFieldType.STRING).description("brief description"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("detailed description"),
                        fieldWithPath("pricePerMonth").type(JsonFieldType.NUMBER).description("price per month"),
                        fieldWithPath("version").type(JsonFieldType.STRING).description("version")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void update() throws Exception {
        Category category = category(2L, "category-02");
        Software software = software(1L, "software-rename-01", category);
        software.setStatus(Software.Status.Pending);
        software.setPricePerMonth(1500L);
        software.setVersion("2.0");

        Category c = new Category();
        c.setId(category.getId());

        Software s = new Software();
        s.setId(software.getId());
        s.setName(software.getName());
        s.setCategory(c);
        s.setApp(software.getApp());
        s.setManifest(software.getManifest());
        s.setIcon(software.getIcon());
        s.setScreenshotList(software.getScreenshotList());
        s.setSummary(software.getSummary());
        s.setDescription(software.getDescription());
        s.setType(software.getType());
        s.setPricePerMonth(software.getPricePerMonth());
        s.setVersion(software.getVersion());
        s.setInUse(software.getInUse());

        given(softwareService.update(any(Software.class))).willReturn(software);
        given(softwareService.get(eq(1L))).willReturn(software);

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.put("/softwares/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId)
                        .content(objectMapper.writeValueAsString(s)).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/software/update",
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
                        parameterWithName("id").description("Category's id")
                    ),
                requestParameters(
                    ),
                relaxedRequestFields(
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("summary").type(JsonFieldType.STRING).description("brief description"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("detailed description"),
                        fieldWithPath("inUse").type(JsonFieldType.STRING).description(String.format("usage status (%s)", StringUtils.arrayToCommaDelimitedString(Yn.values())))
                    ),
                relaxedResponseFields(                        
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(String.format("status (%s)", StringUtils.arrayToCommaDelimitedString(Software.Status.values()))),
                        fieldWithPath("inUse").type(JsonFieldType.STRING).description(String.format("usage status (%s)", StringUtils.arrayToCommaDelimitedString(Yn.values()))),
                        fieldWithPath("category").type(JsonFieldType.OBJECT).description("category"),
                        fieldWithPath("app").type(JsonFieldType.STRING).description("app file"),
                        fieldWithPath("manifest").type(JsonFieldType.STRING).description("manifest file"),
                        fieldWithPath("icon").type(JsonFieldType.STRING).description("icon file"),
                        fieldWithPath("screenshotList").type(JsonFieldType.ARRAY).description("screenshot files"),
                        fieldWithPath("summary").type(JsonFieldType.STRING).description("brief description"),
                        fieldWithPath("description").type(JsonFieldType.STRING).description("detailed description"),
                        fieldWithPath("pricePerMonth").type(JsonFieldType.NUMBER).description("price per month"),
                        fieldWithPath("version").type(JsonFieldType.STRING).description("version")
                    )
                )
            );
        // @formatter:on
    }

}
