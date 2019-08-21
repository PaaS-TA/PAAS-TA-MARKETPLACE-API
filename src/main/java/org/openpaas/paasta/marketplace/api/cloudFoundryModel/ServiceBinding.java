package org.openpaas.paasta.marketplace.api.cloudFoundryModel;

import lombok.Data;

import java.sql.Date;

@Data
public class ServiceBinding {

    private String guid;
    private String hostname;
    private String port;
    private String username;
    private String password;
    private String uri;
    // private Map<String, Object> credentials;
    // private Application app;
    private String appGuid;
    // private String appName;
    private ServiceInstance serviceInstance;
    private String serviceInstanceGuid;
    private String serviceInstanceName;
    private String organizationGuid;
    private String organizationName;
    private String spaceGuid;
    private String spaceName;
    private String serviceGuid;
    private String serviceLabel;
    private Date created;
    private Date updated;

    public ServiceBinding() {
    }

    public ServiceBinding(String appGuid) {
        this.appGuid = appGuid;
    }

}