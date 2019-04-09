package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Space {
    private Map metadata;
    private Map entity;

    private String orgName;
    private String newSpaceName;


    private String userId;
    private UUID guid;
    private String name;

    @JsonProperty("service_count")
    private int serviceCount = 0;

    @JsonProperty("app_count")
    private int appCount = 0;

    private int appCountStarted = 0;
    private int appCountStopped = 0;
    private int appCountCrashed = 0;

    @JsonProperty("mem_dev_total")
    private int memDevTotal;

    @JsonProperty("mem_prd_total")
    private int memProdTotal;

    private int memoryUsage;
    private int memoryLimit;

    private int spaceId;
    private int orgId;
    private String spaceGuid;
    private String orgGuid;
    private Date created;
    private Date lastModified;

    private boolean recursive;

    private List<App> apps = new ArrayList<App>();

    private List<Service> services = new ArrayList<Service>();

}
