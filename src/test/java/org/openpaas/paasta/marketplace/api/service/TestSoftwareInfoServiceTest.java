package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.TestSoftwareInfo;
import org.openpaas.paasta.marketplace.api.repository.TestSoftwareInfoRepository;

public class TestSoftwareInfoServiceTest extends AbstractMockTest {

    TestSoftwareInfoService testSoftwareInfoService;

    @Mock
    TestSoftwareInfoRepository testSoftwareInfoRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        testSoftwareInfoService = new TestSoftwareInfoService(testSoftwareInfoRepository);
    }

    @Test
    public void create() {
        TestSoftwareInfo testSoftware1 = testSoftwareInfo(1L, 21L, 31L);

        given(testSoftwareInfoRepository.save(any(TestSoftwareInfo.class))).willReturn(testSoftware1);

        TestSoftwareInfo result = testSoftwareInfoService.create(testSoftware1);
        assertEquals(testSoftware1, result);

        verify(testSoftwareInfoRepository).save(any(TestSoftwareInfo.class));
    }

    @Test
    public void getTestSwInfoList() {
        TestSoftwareInfo testSoftware1 = testSoftwareInfo(1L, 21L, 31L);
        TestSoftwareInfo testSoftware2 = testSoftwareInfo(1L, 21L, 32L);

        List<TestSoftwareInfo> testSoftwareList = new ArrayList<TestSoftwareInfo>();
        testSoftwareList.add(testSoftware1);
        testSoftwareList.add(testSoftware2);

        given(testSoftwareInfoRepository.findBySoftwareId(any(Long.class))).willReturn(testSoftwareList);

        List<TestSoftwareInfo> result = testSoftwareInfoService.getTestSwInfoList(21L);
        assertEquals(testSoftwareList, result);

        verify(testSoftwareInfoRepository).findBySoftwareId(any(Long.class));
    }

    @Test
    public void deleteDeployTestApp() {
        testSoftwareInfoService.deleteDeployTestApp(7L);

        verify(testSoftwareInfoRepository).deleteById(7L);
    }

}
