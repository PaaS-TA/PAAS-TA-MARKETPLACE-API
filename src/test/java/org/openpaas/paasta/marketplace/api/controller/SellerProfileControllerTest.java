package org.openpaas.paasta.marketplace.api.controller;

import static org.mockito.ArgumentMatchers.any;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.domain.Profile;
import org.openpaas.paasta.marketplace.api.domain.ProfileSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.repository.UserRepository;
import org.openpaas.paasta.marketplace.api.service.ProfileService;
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
@WebMvcTest(SellerProfileController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
public class SellerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ProfileService profileService;

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

    private Profile profile(String id) {
        return profile(id, id);
    }

    private Profile profile(String id, String name) {
        Profile profile = new Profile();
        profile.setId(id);
        profile.setName(name);
        profile.setType(Profile.Type.Personal);
        profile.setManager(name);
        profile.setEmail(id);
        profile.setSiteUrl("mycompany.com");
        profile.setCreatedBy(userId);
        profile.setCreatedDate(current);
        profile.setLastModifiedBy(userId);
        profile.setLastModifiedDate(current);

        return profile;
    }

    @Test
    public void get() throws Exception {
        Profile profile = profile(userId);

        given(profileService.get(any(String.class))).willReturn(profile);

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/profiles/{id}", userId).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/profile/get",
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
                        parameterWithName("id").description("Profile's id")
                    ),
                requestParameters(
                    ),
                relaxedResponseFields(
                        fieldWithPath("id").type(JsonFieldType.STRING).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description(String.format("status (%s)", StringUtils.arrayToCommaDelimitedString(Profile.Type.values()))),
                        fieldWithPath("manager").type(JsonFieldType.STRING).description("manager"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                        fieldWithPath("siteUrl").type(JsonFieldType.STRING).description("site url")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void create() throws Exception {
        Profile profile = profile(userId);

        Profile p = new Profile();
        p.setId(profile.getId());
        p.setName(profile.getName());
        p.setType(profile.getType());
        p.setManager(profile.getManager());
        p.setEmail(profile.getEmail());
        p.setSiteUrl(profile.getSiteUrl());

        given(profileService.create(any(Profile.class))).willReturn(profile);

        ResultActions result = this.mockMvc
                .perform(RestDocumentationRequestBuilders.post("/profiles").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId)
                        .content(objectMapper.writeValueAsString(p)).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/profile/create",
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
                        fieldWithPath("id").type(JsonFieldType.STRING).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description(String.format("type (%s)", StringUtils.arrayToCommaDelimitedString(Profile.Type.values()))),
                        fieldWithPath("manager").type(JsonFieldType.STRING).description("manager"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                        fieldWithPath("siteUrl").type(JsonFieldType.STRING).description("site url")
                    ),
                relaxedResponseFields(                        
                        fieldWithPath("id").type(JsonFieldType.STRING).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description(String.format("type (%s)", StringUtils.arrayToCommaDelimitedString(Profile.Type.values()))),
                        fieldWithPath("manager").type(JsonFieldType.STRING).description("manager"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                        fieldWithPath("siteUrl").type(JsonFieldType.STRING).description("site url")
                    )
                )
            );
        // @formatter:on
    }

    @Test
    public void update() throws Exception {
        Profile profile = profile(userId);
        profile.setName("yourname");
        profile.setEmail("yourname@yourcompany.com");
        profile.setType(Profile.Type.Company);

        Profile p = new Profile();
        p.setId(profile.getId());
        p.setName(profile.getName());
        p.setType(profile.getType());
        p.setManager(profile.getManager());
        p.setEmail(profile.getEmail());
        p.setSiteUrl(profile.getSiteUrl());

        given(profileService.update(any(Profile.class))).willReturn(profile);

        ResultActions result = this.mockMvc.perform(
                RestDocumentationRequestBuilders.put("/profiles/{id}", userId).contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON).header("Authorization", userId)
                        .content(objectMapper.writeValueAsString(p)).characterEncoding("utf-8"));

        result.andExpect(status().isOk());
        result.andDo(print());

        // @formatter:off
        result.andDo(
            document("user/profile/update",
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
                        parameterWithName("id").description("Profile's id")
                    ),
                requestParameters(
                    ),
                relaxedRequestFields(
                        fieldWithPath("id").type(JsonFieldType.STRING).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description(String.format("type (%s)", StringUtils.arrayToCommaDelimitedString(Profile.Type.values()))),
                        fieldWithPath("manager").type(JsonFieldType.STRING).description("manager"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                        fieldWithPath("siteUrl").type(JsonFieldType.STRING).description("site url")
                    ),
                relaxedResponseFields(                        
                        fieldWithPath("id").type(JsonFieldType.STRING).description("id (PK)"),
                        fieldWithPath("name").type(JsonFieldType.STRING).description("name"),
                        fieldWithPath("type").type(JsonFieldType.STRING).description(String.format("type (%s)", StringUtils.arrayToCommaDelimitedString(Profile.Type.values()))),
                        fieldWithPath("manager").type(JsonFieldType.STRING).description("manager"),
                        fieldWithPath("email").type(JsonFieldType.STRING).description("email"),
                        fieldWithPath("siteUrl").type(JsonFieldType.STRING).description("site url")
                    )
                )
            );
        // @formatter:on
    }
    
    @Test
    public void getPage() throws Exception {
        Profile profile = profile(userId);
        profile.setName("yourname");
        profile.setEmail("yourname@yourcompany.com");
        profile.setType(Profile.Type.Company);

        Pageable pageable = PageRequest.of(0, 10);
        List<Profile> content = new ArrayList<>();
        content.add(profile);
        Page<Profile> page = new PageImpl<>(content, pageable, content.size());
        
        given(profileService.getPage(any(ProfileSpecification.class), any(Pageable.class))).willReturn(page);
        
        ResultActions result = this.mockMvc.perform(RestDocumentationRequestBuilders.get("/profiles/page")
										                .param("page", "0")
										                .param("size", "10")
										                .param("sort", "id,asc")
										                .contentType(MediaType.APPLICATION_JSON)
										                .accept(MediaType.APPLICATION_JSON).header("Authorization", userId)
										                .characterEncoding("utf-8"));
        result.andExpect(status().isOk());
        result.andDo(print());
        result.andDo(
            document("/profiles/getPage",
                preprocessRequest(
                    modifyUris().scheme("http")
		                        .host("marketplace.yourdomain.com")
		                        .removePort()
                    ,prettyPrint()
                ),
                preprocessResponse(
                    prettyPrint()
                ),
                pathParameters(),
                requestParameters(
                    parameterWithName("page").description("index of page (starting from 0)"),
                    parameterWithName("size").description("size of page"),
                    parameterWithName("sort").description("sort condition (column,direction)")
                ),
                relaxedResponseFields(
                    fieldWithPath("content").type(JsonFieldType.ARRAY).description("content of page"),
                    fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("request pageable"),
                    fieldWithPath("numberOfElements").type(JsonFieldType.NUMBER).description("total count of elements")
                )
            )
        );
    }

}
