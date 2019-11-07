package org.openpaas.paasta.marketplace.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openpaas.paasta.marketplace.api.domain.Category;
import org.openpaas.paasta.marketplace.api.domain.InstanceCart;
import org.openpaas.paasta.marketplace.api.domain.InstanceCartSpecification;
import org.openpaas.paasta.marketplace.api.domain.Software;
import org.openpaas.paasta.marketplace.api.repository.InstanceCartRepository;

public class InstanceCartServiceTest extends AbstractMockTest {

    InstanceCartService instanceCartService;

    @Mock
    InstanceCartRepository instanceCartRepository;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        instanceCartService = new InstanceCartService(instanceCartRepository);
    }

    @Test
    public void create() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        InstanceCart instanceCart1 = instanceCart(1L, software1);

        given(instanceCartRepository.save(any(InstanceCart.class))).willReturn(instanceCart1);

        InstanceCart result = instanceCartService.create(instanceCart1);
        assertEquals(instanceCart1, result);

        verify(instanceCartRepository).save(any(InstanceCart.class));
    }

    @Test
    public void getAllList() {
        InstanceCartSpecification spec = new InstanceCartSpecification();

        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        InstanceCart instanceCart1 = instanceCart(1L, software1);
        InstanceCart instanceCart2 = instanceCart(2L, software1);

        List<InstanceCart> instanceCartList = new ArrayList<InstanceCart>();
        instanceCartList.add(instanceCart1);
        instanceCartList.add(instanceCart2);

        given(instanceCartRepository.findAll(any(InstanceCartSpecification.class))).willReturn(instanceCartList);

        List<InstanceCart> result = instanceCartService.getAllList(spec);
        assertEquals(instanceCartList, result);

        verify(instanceCartRepository).findAll(any(InstanceCartSpecification.class));
    }

    @Test
    public void getUserAllCartList() {
        Category category1 = category(1L, "category-01");
        Software software1 = software(1L, "software-01", category1);
        InstanceCart instanceCart1 = instanceCart(1L, software1);
        InstanceCart instanceCart2 = instanceCart(2L, software1);

        List<InstanceCart> instanceCartList = new ArrayList<InstanceCart>();
        instanceCartList.add(instanceCart1);
        instanceCartList.add(instanceCart2);

        List<Object[]> valuesList = new ArrayList<>();
        for (InstanceCart instanceCart : instanceCartList) {
            Object[] values = new String[8];
            values[0] = String.valueOf(instanceCart.getId());
            values[1] = String.valueOf(instanceCart.getSoftware().getId());
            values[2] = String.valueOf(instanceCart.getSoftware().getName());
            values[3] = String.valueOf(instanceCart.getSoftware().getVersion());
            values[4] = String.valueOf(instanceCart.getSoftware().getCategory().getId());
            values[5] = String.valueOf(instanceCart.getSoftware().getCategory().getName());
            values[6] = String.valueOf(instanceCart.getSoftwarePlanAmtMonth());
            values[7] = String.valueOf(instanceCart.getPricePerInstance());
            valuesList.add(values);
        }

        given(instanceCartRepository.userAllCartList(any(String.class), any(String.class), any(String.class)))
                .willReturn(valuesList);

        List<InstanceCart> result = instanceCartService.getUserAllCartList(userId, "20191107", "20191108");
        assertEquals(instanceCartList.size(), result.size());

        verify(instanceCartRepository).userAllCartList(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void allDelete() {
        InstanceCartSpecification spec = new InstanceCartSpecification();
        spec.setCreatedBy(userId);

        instanceCartService.allDelete(spec);

        spec.setCreatedBy(null);

        instanceCartService.allDelete(spec);

        verify(instanceCartRepository, atLeastOnce()).deleteAllByUserIdInQuery(any(String.class));
    }

    @Test
    public void delete() {
        InstanceCartSpecification spec = new InstanceCartSpecification();
        spec.setCreatedBy(userId);
        List<Long> ids = new ArrayList<>();
        ids.add(1L);
        ids.add(2L);
        ids.add(3L);
        spec.setInInstanceCartId(ids);

        given(instanceCartRepository.delete(any(), any())).willReturn(0);

        instanceCartService.delete(spec);

        spec.setInInstanceCartId(null);

        instanceCartService.delete(spec);

        spec.setInInstanceCartId(new ArrayList<>());

        instanceCartService.delete(spec);

        spec.setCreatedBy(null);

        instanceCartService.delete(spec);

        verify(instanceCartRepository, atLeastOnce()).delete(any(), any());
    }

}
