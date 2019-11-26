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
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.Yn;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.openpaas.paasta.marketplace.api.service.InstanceService;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
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
@WebMvcTest(InstanceController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class InstanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private InstanceService instanceService;

    @MockBean
    SoftwarePlanService softwarePlanService;

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

    private Instance instance(Long id, Software software) {
        Instance instance = new Instance();
        instance.setId(id);
        instance.setSoftware(software);
        instance.setStatus(Instance.Status.Approval);
        instance.setProvisionStatus(Instance.ProvisionStatus.Pending);
        instance.setUsageStartDate(current);
        instance.setCreatedBy(userId);
        instance.setCreatedDate(current);
        instance.setLastModifiedBy(userId);
        instance.setLastModifiedDate(current);
        instance.setSoftwarePlanId("1");

        return instance;
    }

    @Test
    public void getPage() throws Exception {
        Category category1 = category(1L, "category-01");
        Category category2 = category(2L, "category-02");
        Software software1 = software(1L, "software-01", category1);
        Software software2 = software(2L, "software-02", category2);
        software2.setCreatedBy("bar");
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software2);

        Pageable pageable = PageRequest.of(0, 10);

        List<Instance> content = new ArrayList<>();
        content.add(instance1);
        content.add(instance2);
        Page<Instance> page = new PageImpl<>(content, pageable, content.size());

        given(instanceService.getPage(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/instances/my/page")
                .param("page", "0").param("size", "10").param("sort", "id,asc")
                .param("status", Instance.Status.Approval.toString()).param("categoryId", "1")
                .param("softwareNameLike", "software").contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/instance/my-page",
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
                        parameterWithName("status").description("status"),
                        parameterWithName("categoryId").description("category's id"),
                        parameterWithName("softwareNameLike").description("search word of software's name")
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
        Instance instance = instance(1L, software);

        given(instanceService.get(eq(1L))).willReturn(instance);

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/instances/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/instance/get",
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
                        parameterWithName("id").description("Instance's id")
                    ),
                requestParameters(
                    ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("software").type(JsonFieldType.OBJECT).description("software"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(String.format("status (%s)", StringUtils.arrayToCommaDelimitedString(Instance.Status.values())))
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void create() throws Exception {
        Category category = category(1L, "category-01");
        Software software = software(1L, "software-01", category);
        Instance instance = instance(1L, software);

        Software s = new Software();
        s.setId(software.getId());
        Instance i = new Instance();
        i.setSoftware(s);
        i.setSoftwarePlanId(instance.getSoftwarePlanId());

        given(instanceService.create(any(Instance.class))).willReturn(instance);

        ResultActions result = this.mockMvc.perform(
        	RestDocumentationRequestBuilders.post("/instances")
					        				.contentType(MediaType.APPLICATION_JSON)
					                        .accept(MediaType.APPLICATION_JSON)
					                        .header("Authorization", userId)
					                        .content(objectMapper.writeValueAsString(i))
					                        .characterEncoding("utf-8")
        );

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/instance/create",
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
                        fieldWithPath("software").type(JsonFieldType.OBJECT).description("software"),
                        fieldWithPath("software.id").type(JsonFieldType.NUMBER).description("software.id")
                    ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("id (PK)"),
                        fieldWithPath("software").type(JsonFieldType.OBJECT).description("software"),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(String.format("status (%s)", StringUtils.arrayToCommaDelimitedString(Instance.Status.values())))
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void delete() throws Exception {
        Category category = category(1L, "category-01");
        Software software = software(1L, "software-01", category);
        Instance instance = instance(1L, software);
        instance.setStatus(Instance.Status.Deleted);
        instance.setUsageEndDate(LocalDateTime.now());

        given(instanceService.updateToDeleted(eq(1L))).willReturn(instance);
        given(instanceService.get(eq(1L))).willReturn(instance);

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.delete("/instances/{id}", 1L).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/instance/delete",
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
                        parameterWithName("id").description("Instance's id")
                    ),
                requestParameters(
                    ),
                relaxedResponseFields(
                    )
                )
            );
        // @formatter:on
    }

}
