package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.Instance;
import org.openpaas.paasta.marketplace.api.domain.Instance.Status;
import org.openpaas.paasta.marketplace.api.domain.InstanceSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.exception.PlatformException;
import org.openpaas.paasta.marketplace.api.repository.InstanceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

public class InstanceServiceTest extends AbstractMockTest {

    InstanceService instanceService;

    @Mock
    InstanceRepository instanceRepository;

    @Mock
    PlatformService platformService;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        instanceService = new InstanceService(instanceRepository, platformService);

        ReflectionTestUtils.setField(instanceService, "provisioningTryCount", 3);
        ReflectionTestUtils.setField(instanceService, "deprovisioningTryCount", 3);
    }

    @Test
    public void create() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        given(instanceRepository.save(any(Instance.class))).willReturn(instance1);

        Instance result = instanceService.create(instance1);
        assertEquals(instance1, result);

        verify(instanceRepository).save(any(Instance.class));
    }

    @Test
    public void getPage() {
        InstanceSpecification spec = new InstanceSpecification();
        Pageable pageRequest = PageRequest.of(0, 10);

        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Page<Instance> result = instanceService.getPage(spec, pageRequest);
        assertEquals(page, result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void get() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Instance result = instanceService.get(1L);
        assertEquals(instance1, result);

        verify(instanceRepository).findById(1L);
    }

    @Test
    public void updateToDeleted() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Instance result = instanceService.updateToDeleted(1L);
        assertEquals(Status.Deleted, result.getStatus());

        verify(instanceRepository).findById(1L);
    }

    @Test
    public void provision() throws InterruptedException, ExecutionException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Future<Instance> result = instanceService.provision(1L);
        assertEquals(Instance.ProvisionStatus.Successful, result.get().getProvisionStatus());

        verify(instanceRepository).findById(1L);
    }

    @Test
    public void provisionWithError() throws InterruptedException, ExecutionException, PlatformException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        doThrow(new PlatformException()).when(platformService).deprovision(any(Instance.class));
        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Future<Instance> result = instanceService.provision(1L);
        assertEquals(Instance.ProvisionStatus.Pending, result.get().getProvisionStatus());

        verify(instanceRepository).findById(1L);
    }

    @Test
    public void provisionWithErrorOver() throws InterruptedException, ExecutionException, PlatformException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        doThrow(new PlatformException()).when(platformService).deprovision(any(Instance.class));
        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Future<Instance> result = null;
        for (int i = 0; i < 3; i++) {
            result = instanceService.provision(1L);
        }
        assertEquals(Instance.ProvisionStatus.Failed, result.get().getProvisionStatus());

        verify(instanceRepository, atLeastOnce()).findById(1L);
    }

    @Test
    public void provisionWithNoCfInstnce() throws InterruptedException, ExecutionException, PlatformException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        doThrow(new PlatformException("noCfAppInstance")).when(platformService).deprovision(any(Instance.class));
        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Future<Instance> result = instanceService.provision(1L);
        assertEquals(Instance.ProvisionStatus.Successful, result.get().getProvisionStatus());

        verify(instanceRepository).findById(1L);
    }

    @Test
    public void deprovision() throws InterruptedException, ExecutionException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Instance result = instanceService.deprovision(1L);
        assertEquals(Instance.ProvisionStatus.Successful, result.getDeprovisionStatus());

        verify(instanceRepository).findById(1L);
    }

    @Test
    public void deprovisionWithError() throws InterruptedException, ExecutionException, PlatformException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        doThrow(new PlatformException()).when(platformService).deprovision(any(Instance.class));
        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Instance result = instanceService.deprovision(1L);
        assertEquals(Instance.ProvisionStatus.Pending, result.getDeprovisionStatus());

        verify(instanceRepository).findById(1L);
    }

    @Test
    public void deprovisionWithErrorOver() throws InterruptedException, ExecutionException, PlatformException {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);

        doThrow(new PlatformException()).when(platformService).deprovision(any(Instance.class));
        given(instanceRepository.findById(1L)).willReturn(Optional.of(instance1));

        Instance result = null;
        for (int i = 0; i < 3; i++) {
            result = instanceService.deprovision(1L);
        }
        assertEquals(Instance.ProvisionStatus.Failed, result.getDeprovisionStatus());

        verify(instanceRepository, atLeastOnce()).findById(1L);
    }

    @Test
    public void countOfProvisioning() {
        given(instanceRepository.count(any(InstanceSpecification.class))).willReturn(7L);

        long result = instanceService.countOfProvisioning();

        assertEquals(7L, result);

        verify(instanceRepository).count(any(InstanceSpecification.class));
    }

    @Test
    public void getOneToReadyDeprovision() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToReadyDeprovision();

        assertEquals(instance1, result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void getOneToReadyDeprovisionWithNull() {
        List<Instance> instanceList = new ArrayList<Instance>();

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToReadyDeprovision();

        assertNull(result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void getOneToDeprovision() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToDeprovision();

        assertEquals(instance1, result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void getOneToDeprovisionWithNull() {
        List<Instance> instanceList = new ArrayList<Instance>();

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToDeprovision();

        assertNull(result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void countOfDeprovisioning() {
        given(instanceRepository.count(any(InstanceSpecification.class))).willReturn(2L);

        long result = instanceService.countOfDeprovisioning();

        assertEquals(2L, result);

        verify(instanceRepository).count(any(InstanceSpecification.class));
    }

    @Test
    public void getOneToReadyProvision() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToReadyProvision();

        assertEquals(instance1, result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void getOneToReadyProvisionWithNull() {
        List<Instance> instanceList = new ArrayList<Instance>();

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToReadyProvision();

        assertNull(result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void getOneToProvision() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToProvision();

        assertEquals(instance1, result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void getOneToProvisionWithNull() {
        List<Instance> instanceList = new ArrayList<Instance>();

        Page<Instance> page = new PageImpl<>(instanceList);

        given(instanceRepository.findAll(any(InstanceSpecification.class), any(Pageable.class))).willReturn(page);

        Instance result = instanceService.getOneToProvision();

        assertNull(result);

        verify(instanceRepository).findAll(any(InstanceSpecification.class), any(Pageable.class));
    }

    @Test
    public void stopProvisioningWithTimeoutTrue() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        given(instanceRepository.findAll(any(InstanceSpecification.class))).willReturn(instanceList);

        instanceService.stopProvisioning(true);

        verify(instanceRepository).findAll(any(InstanceSpecification.class));
    }

    @Test
    public void stopProvisioningWithTimeoutFalse() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        given(instanceRepository.findAll(any(InstanceSpecification.class))).willReturn(instanceList);

        instanceService.stopProvisioning(false);

        verify(instanceRepository).findAll(any(InstanceSpecification.class));
    }

    @Test
    public void stopDeprovisioningWithTimeoutTrue() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        given(instanceRepository.findAll(any(InstanceSpecification.class))).willReturn(instanceList);

        instanceService.stopDeprovisioning(true);

        verify(instanceRepository).findAll(any(InstanceSpecification.class));
    }

    @Test
    public void stopDeprovisioningWithTimeoutFalse() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        Instance instance1 = instance(1L, software1);
        Instance instance2 = instance(2L, software1);

        List<Instance> instanceList = new ArrayList<Instance>();
        instanceList.add(instance1);
        instanceList.add(instance2);

        given(instanceRepository.findAll(any(InstanceSpecification.class))).willReturn(instanceList);

        instanceService.stopDeprovisioning(false);

        verify(instanceRepository).findAll(any(InstanceSpecification.class));
    }

    @Test
    public void usagePriceTotal() {
        given(instanceRepository.usagePriceTotal(any(String.class), any(String.class), any(String.class)))
                .willReturn(7L);

        long result = instanceService.usagePriceTotal(userId, "20191107", "20191108");

        assertEquals(7L, result);

        verify(instanceRepository).usagePriceTotal(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void getPricePerInstanceList() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        List<Object[]> list = new ArrayList<>();
        for (Long id : ids) {
            String key = String.valueOf(id);
            String value = String.valueOf(id * 10);
            list.add(new String[] { key, value });
        }

        given(instanceRepository.pricePerInstanceList(anyList(), any(String.class), any(String.class)))
                .willReturn(list);

        Map<String, String> result = instanceService.getPricePerInstanceList(ids, "20191107", "20191108");

        assertEquals("10", result.get("1"));
        assertEquals("20", result.get("2"));
        assertEquals("30", result.get("3"));

        verify(instanceRepository).pricePerInstanceList(anyList(), any(String.class), any(String.class));
    }

    @Test
    public void getPricePerInstanceListWithNull() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        given(instanceRepository.pricePerInstanceList(anyList(), any(String.class), any(String.class)))
                .willReturn(null);

        Map<String, String> result = instanceService.getPricePerInstanceList(ids, "20191107", "20191108");

        assertEquals(0, result.size());

        verify(instanceRepository).pricePerInstanceList(anyList(), any(String.class), any(String.class));
    }

    @Test
    public void getPricePerInstanceListWithEmpty() {
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);

        List<Object[]> list = new ArrayList<>();

        given(instanceRepository.pricePerInstanceList(anyList(), any(String.class), any(String.class)))
                .willReturn(list);

        Map<String, String> result = instanceService.getPricePerInstanceList(ids, "20191107", "20191108");

        assertEquals(0, result.size());

        verify(instanceRepository).pricePerInstanceList(anyList(), any(String.class), any(String.class));
    }

    @Test
    public void getSoftwareUsagePriceTotal() {
        given(instanceRepository.softwareUsagePriceTotal(any(Long.class))).willReturn(7L);

        long result = instanceService.getSoftwareUsagePriceTotal(1L);

        assertEquals(7L, result);

        verify(instanceRepository).softwareUsagePriceTotal(any(Long.class));
    }

}
