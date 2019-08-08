package org.openpaas.paasta.marketplace.api.client;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openpaas.paasta.marketplace.api.domain.CustomPage;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("local")
@Slf4j
public class SoftwareTest {

    RestTemplate rest;

    @Before
    public void setUp() throws Exception {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new HeaderRequestInterceptor("Accept", MediaType.APPLICATION_JSON_VALUE));
        interceptors.add(new HeaderRequestInterceptor("Authorization", "foo"));

        rest = new RestTemplate();
        rest.setInterceptors(interceptors);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test(expected = Throwable.class)
    @SuppressWarnings("unchecked")
    public void getPageToPage() {
        Page<Software> page = rest.getForObject("http://localhost:8000/softwares/page", Page.class);

        log.info("page={}", page);

        assertNotNull(page);
    }

    @Test(expected = Throwable.class)
    @SuppressWarnings("unchecked")
    public void getPageToPageImpl() {
        Page<Software> page = rest.getForObject("http://localhost:8000/softwares/page", PageImpl.class);

        log.info("page={}", page);

        assertNotNull(page);
    }

    @Test
    @SuppressWarnings({ "unchecked", "deprecation" })
    public void getPageToCustomPage() {
        CustomPage<Software> customPage = rest.getForObject("http://localhost:8000/softwares/page", CustomPage.class);
        Page<Software> page = customPage.toPage();

        log.info("customPage={}", customPage);
        log.info("page={}", page);
        log.info("page.getContent().get(0)={}", page.getContent().get(0));

        assertNotNull(page);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void getPageToType() {
        ResponseEntity<CustomPage<Software>> responseEntity = rest.exchange("http://localhost:8000/softwares/page",
                HttpMethod.GET, null, new ParameterizedTypeReference<CustomPage<Software>>() {
                });
        CustomPage<Software> customPage = responseEntity.getBody();
        Page<Software> page = customPage.toPage();

        log.info("customPage={}", customPage);
        log.info("page={}", page);
        log.info("page.getContent().get(0)={}", page.getContent().get(0));

        Software software = page.getContent().get(0);
        log.info("software={}", software);
        Software sameSoftware = customPage.getContent().get(0);
        log.info("sameSoftware={}", sameSoftware);

        assertNotNull(page);
    }

    @Test
    public void getPageToGeneralType() {
        ResponseEntity<CustomPage<Software>> responseEntity = rest.exchange("http://localhost:8000/softwares/page",
                HttpMethod.GET, null, new ParameterizedTypeReference<CustomPage<Software>>() {
                });
        Page<Software> page = responseEntity.getBody();

        log.info("page={}", page);
        log.info("page.getContent().get(0)={}", page.getContent().get(0));

        Software software = page.getContent().get(0);
        log.info("software={}", software);

        assertNotNull(page);
    }

    @Test
    public void get() {
        Software software = rest.getForObject("http://localhost:8000/softwares/{id}", Software.class, 1L);

        log.info("software={}", software);

        assertNotNull(software);
    }

}
