package org.openpaas.paasta.marketplace.api.cloudFoundryModel;

import lombok.Data;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class App {
    private String guid;

    private String name;

    private String buildpack;

    private String detectedBuildpack;

    private Integer memory;

    private Integer instances;

    private Integer diskQuota;

    private String state;

    private String packageState;

    private String spaceGuid;

    private String spaceName;

    private String organizationGuid;

    private String organizationName;

    private String domainId;

    private String dockerImage;

    private String pushType;

    private List<ServiceBinding> serviceBindings;

    private List<String> uris;

    private Map<String, Object> env;

    private int health;

    private Date created;

    private Date updated;

    //------- 쓸데없으면 안채워쥐겠지!
    private File file;
    private String hostName;
    private String appName;
    private List<Route> routes;
    private Map<String, Object> environmentJson;
}
