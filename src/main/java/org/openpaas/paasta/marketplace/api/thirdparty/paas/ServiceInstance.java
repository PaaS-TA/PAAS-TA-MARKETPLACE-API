package org.openpaas.paasta.marketplace.api.thirdparty.paas;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class ServiceInstance {

	private List<ServiceBinding> serviceBindings = new ArrayList<>();
    private String name;
    private String guid;
    private String servicePlanGuid;
    private String spaceGuid;

    public ServiceInstance() {
    }

    public ServiceInstance(String AppGuid) {
        this.serviceBindings.add(new ServiceBinding(AppGuid));
    }

}
