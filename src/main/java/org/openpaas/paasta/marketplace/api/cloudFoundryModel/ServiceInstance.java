package org.openpaas.paasta.marketplace.api.cloudFoundryModel;

import lombok.Data;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceInstance {

    private List<ServiceBinding> serviceBindings = new ArrayList<>();
    private String name;
    private String guid;
    private String servicePlanGuid;
    
    @Transient
    private String spaceGuid;

    public ServiceInstance() {
    }

    public ServiceInstance(String AppGuid) {
        this.serviceBindings.add(new ServiceBinding(AppGuid));
    }

}
