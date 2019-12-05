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

import org.cloudfoundry.operations.domains.Status;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistory;
import org.openpaas.paasta.marketplace.api.domain.SoftwareHistorySpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlan;
import org.openpaas.paasta.marketplace.api.domain.SoftwarePlanSpecification;
import org.openpaas.paasta.marketplace.api.domain.SoftwareSpecification;
import org.openpaas.paasta.marketplace.api.domain.TestSoftwareInfo;
import org.openpaas.paasta.marketplace.api.domain.Yn;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.openpaas.paasta.marketplace.api.service.PlatformService;
import org.openpaas.paasta.marketplace.api.service.SoftwarePlanService;
import org.openpaas.paasta.marketplace.api.service.SoftwareService;
import org.openpaas.paasta.marketplace.api.service.TestSoftwareInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @MockBean
    SoftwarePlanService softwarePlanService;

    @MockBean
    PlatformService platformService;

    @MockBean
    TestSoftwareInfoService testSoftwareInfoService;

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
        software.setDescription("description of this software.");
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
    
    private SoftwareHistory softwareHistory(Long id, String name) {
    	SoftwareHistory softwareHistory = new SoftwareHistory();
    	softwareHistory.setId(id);
    	softwareHistory.setDescription("description of this software.");
    	softwareHistory.setCreatedBy(userId);
    	softwareHistory.setCreatedDate(current);
    	softwareHistory.setLastModifiedBy(userId);
    	softwareHistory.setLastModifiedDate(current);
    	softwareHistory.setInUse(Yn.Y);
    	
    	return softwareHistory;
    }
    
    private SoftwarePlan softwarePlan(Long id, Long softwareId, String name) {
    	SoftwarePlan softwarePlan = new SoftwarePlan();
    	softwarePlan.setId(id);
    	softwarePlan.setSoftwareId(softwareId);
    	softwarePlan.setApplyMonth(current.toString());
    	softwarePlan.setName(name);
    	softwarePlan.setDescription("description of this softwarePlan.");
    	softwarePlan.setMemorySize("1G");
    	softwarePlan.setDiskSize("2G");
    	softwarePlan.setCpuAmt(1000);
    	softwarePlan.setMemoryAmt(1000);
    	softwarePlan.setDiskAmt(1000);
    	
    	return softwarePlan;
    }
    
    private TestSoftwareInfo testSoftwareInfo(Long id, Long softwareId, String name) {
    	TestSoftwareInfo testSoftwareInfo = new TestSoftwareInfo();
    	testSoftwareInfo.setId(id);
    	testSoftwareInfo.setName(name);
    	testSoftwareInfo.setSoftwareId(softwareId);
    	testSoftwareInfo.setAppGuid(UUID.randomUUID().toString());
    	testSoftwareInfo.setSoftwarePlanId(1L);
    	testSoftwareInfo.setStatus(TestSoftwareInfo.Status.Successful);
    	return testSoftwareInfo;
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
    public void updateMetadata() throws Exception {
        Category category = category(2L, "category-02");
        Software software = software(1L, "software-rename-01", category);
        software.setStatus(Software.Status.Approval);
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
                        fieldWithPath("inUse").type(JsonFieldType.STRING).description(String.format("usage status (%s)", StringUtils.arrayToCommaDelimitedString(Yn.values()))),
                        fieldWithPath("status").type(JsonFieldType.STRING).description(String.format("status (%s)", StringUtils.arrayToCommaDelimitedString(Software.Status.values()))),
                        fieldWithPath("confirmComment").type(JsonFieldType.STRING).description("reason for approval or rejected")
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

    // 상품 히스토리 내역 조회
    @Test
    public void getHistoryList() throws Exception {
        SoftwareHistory softwareHistory1 = softwareHistory(1L, "software-01");
        SoftwareHistory softwareHistory2 = softwareHistory(2L, "software-02");
        List<SoftwareHistory> softwareHistoryList = new ArrayList<SoftwareHistory>();
        softwareHistoryList.add(softwareHistory1);
        softwareHistoryList.add(softwareHistory2);

        given(softwareService.getHistoryList(any(SoftwareHistorySpecification.class), any(Sort.class))).willReturn(softwareHistoryList);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/{id}/histories", 1L)
        												.param("sort", "id,asc")
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/getHistoryList",
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
                	parameterWithName("sort").description("sort condition (column,direction)")
                ),
                relaxedResponseFields(
                )
            )
        );
    }

    // 상품가격 리스트 조회
    @Test
    public void currentSoftwarePlanList() throws Exception {
    	SoftwarePlan softwarePlan1 = softwarePlan(1L, 1L, "softwarePlan-01");
    	SoftwarePlan softwarePlan2 = softwarePlan(2L, 1L, "softwarePlan-02");

    	List<SoftwarePlan> softwarePlanList = new ArrayList<SoftwarePlan>();
    	softwarePlanList.add(softwarePlan1);
    	softwarePlanList.add(softwarePlan2);

        given(softwarePlanService.getCurrentSoftwarePlanList(any(SoftwarePlanSpecification.class))).willReturn(softwarePlanList);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/plan/{id}/list", 1L)
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/currentSoftwarePlanList",
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
                )
            )
        );
    }

    // 상품가격 리스트 조회
    @Test
    public void getList() throws Exception {
    	SoftwarePlan softwarePlan1 = softwarePlan(1L, 1L, "softwarePlan-01");
    	SoftwarePlan softwarePlan2 = softwarePlan(2L, 1L, "softwarePlan-02");

    	List<SoftwarePlan> softwarePlanList = new ArrayList<SoftwarePlan>();
    	softwarePlanList.add(softwarePlan1);
    	softwarePlanList.add(softwarePlan2);

        given(softwarePlanService.getList(any(SoftwarePlanSpecification.class), any(Sort.class))).willReturn(softwarePlanList);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/plan/{id}/histories", 1L)
        												.param("sort", "id,asc")
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/getList",
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
            		parameterWithName("sort").description("sort condition (column,direction)")
                ),
                relaxedResponseFields(
                )
            )
        );
    }

    // 상품가격 정보를 적용일자로 조회
    @Test
    public void getApplyMonth() throws Exception {
    	SoftwarePlan softwarePlan1 = softwarePlan(1L, 1L, "softwarePlan-01");
    	SoftwarePlan softwarePlan2 = softwarePlan(2L, 1L, "softwarePlan-02");

    	List<SoftwarePlan> softwarePlanList = new ArrayList<SoftwarePlan>();
    	softwarePlanList.add(softwarePlan1);
    	softwarePlanList.add(softwarePlan2);

        given(softwarePlanService.getList(any(SoftwarePlanSpecification.class), any(Sort.class))).willReturn(softwarePlanList);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/plan/{id}/applyMonth", 1L)
        												.param("applyMonth", current.toString())
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/getApplyMonth",
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
            		parameterWithName("applyMonth").description("softwarePlan apply Date.")
                ),
                relaxedResponseFields(
                )
            )
        );
    }

    // 앱 배포 테스트
    @Test
    public void deployTestSoftware() throws Exception {
    	TestSoftwareInfo testSoftwareInfo = testSoftwareInfo(1L, 1L, "testSoftware-01");
        Category category = category(1L, "category-01");
        Software software = software(1L, "software-01", category);
    	
        given(testSoftwareInfoService.create(any(TestSoftwareInfo.class))).willReturn(testSoftwareInfo);
        given(softwareService.get(any(Long.class))).willReturn(software);
        
        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.post("/admin/softwares/{id}/plan/{planId}", 1L, 1L)
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.content(objectMapper.writeValueAsString(testSoftwareInfo))
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/deployTestSoftware",
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
                    parameterWithName("id").description("Software's id"),
                    parameterWithName("planId").description("SoftwarePlan's id")
                ),
                requestParameters(
                ),
                relaxedResponseFields(
                )
            )
        );
    }

    // 각 상품에 대한 배포 테스한 앱 목록 조회
    // @GetMapping("/{id}/testSwInfo")
    @Test
    public void getTestSwInfoList() throws Exception {
    	TestSoftwareInfo testSoftwareInfo1 = testSoftwareInfo(1L, 1L, "testSoftware-01");
    	TestSoftwareInfo testSoftwareInfo2 = testSoftwareInfo(2L, 1L, "testSoftware-02");

    	List<TestSoftwareInfo> testSoftwareInfoList = new ArrayList<TestSoftwareInfo>();
    	testSoftwareInfoList.add(testSoftwareInfo1);
    	testSoftwareInfoList.add(testSoftwareInfo2);

        given(testSoftwareInfoService.getTestSwInfoList(any(Long.class))).willReturn(testSoftwareInfoList);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/{id}/testSwInfo", 1L)
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/getTestSwInfoList",
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
                )
            )
        );
    }

    // 배포 테스트한 앱 삭제
    // @DeleteMapping("/{swId}/testSwInfo/{id}/app/{appGuid}")
    @Test
    public void deleteDeployTestApp() throws Exception {
    	String appGuid = UUID.randomUUID().toString();
    	
        Category category = category(1L, "category-01");
        Software software = software(1L, "software-01", category);
    	
        given(softwareService.get(any(Long.class))).willReturn(software);

        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/softwares/{swId}/testSwInfo/{id}/app/{appGuid}", 1L, 1L, appGuid)
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/deleteDeployTestApp",
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
                	parameterWithName("swId").description("Software's id"),
                    parameterWithName("id").description("TestSoftware's id"),
                    parameterWithName("appGuid").description("Test App's id")
                ),
                requestParameters(
                ),
                relaxedResponseFields(
                )
            )
        );
    }

    // 배포 테스트 실패한 앱 삭제
    @Test
    public void deleteDeployTestFailedApp() throws Exception {
        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/softwares/testFailed/app/{testFailedAppId}", 1L)
                										.contentType(MediaType.APPLICATION_JSON)
                										.accept(MediaType.APPLICATION_JSON)
                										.header("Authorization", adminId)
                										.characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());

        result.andDo(
            document("admin/software/deleteDeployTestFailedApp",
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
                    parameterWithName("testFailedAppId").description("Test Fail App's id")
                ),
                requestParameters(
                ),
                relaxedResponseFields(
                )
            )
        );
    }

    // 카테고리를 사용하고 있는 소프트웨어 카운트
    @Test
    public void softwareUsedCategoryCount() throws Exception {
    	given(softwareService.getSoftwareUsedCategoryCount(any(Long.class))).willReturn(1L);
    	
        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/softwares/softwareUsedCategoryCount/{categoryid}", 1L)
														.contentType(MediaType.APPLICATION_JSON)
														.accept(MediaType.APPLICATION_JSON)
														.header("Authorization", adminId)
														.characterEncoding("utf-8"));
		result.andExpect(status().isOk());
		result.andDo(print());

        result.andDo(
            document("admin/software/softwareUsedCategoryCount",
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
                    parameterWithName("categoryid").description("Category's id")
                ),
                requestParameters(
                ),
                relaxedResponseFields(
                )
            )
        );
    }
}
