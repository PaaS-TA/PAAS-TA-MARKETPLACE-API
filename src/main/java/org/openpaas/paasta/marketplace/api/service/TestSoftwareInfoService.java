package org.openpaas.paasta.marketplace.api.service;

import lombok.RequiredArgsConstructor;
import org.openpaas.paasta.marketplace.api.domain.TestSoftwareInfo;
import org.openpaas.paasta.marketplace.api.repository.TestSoftwareInfoRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author hrjin
 * @version 1.0
 * @since 2019-10-31
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TestSoftwareInfoService {
    private final TestSoftwareInfoRepository testSoftwareInfoRepository;

    public TestSoftwareInfo create(TestSoftwareInfo testSoftwareInfo) {
        return testSoftwareInfoRepository.save(testSoftwareInfo);
    }

    public List<TestSoftwareInfo> getTestSwInfoList(Long id) {
        return testSoftwareInfoRepository.findBySoftwareId(id);
    }

    public void deleteDeployTestApp(Long id) {
        testSoftwareInfoRepository.deleteById(id);
    }
}
