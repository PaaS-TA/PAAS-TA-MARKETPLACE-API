package org.openpaas.paasta.marketplace.api.thirdparty.paas;

import lombok.Data;

import java.util.List;

@Data
public class Catalog {

    private int no;
    private String name;
    private String classification;
    private String classificationValue;
    private String classificationSummary;
    private String summary;
    private String description;
    private String thumbImgName;
    private String thumbImgPath;
    private String useYn;
    private String userId;
    private String created;
    private String lastModified;
    private String buildPackName;
    private String servicePackName;
    private int starterCategoryNo;
    private int servicePackCategoryNo;
    private int buildPackCategoryNo;
    private String searchKeyword;
    private String searchTypeColumn;
    private String searchTypeUseYn;
    private List<Integer> servicePackCategoryNoList;
    private int catalogNo;
    private String catalogType;
    private String servicePlan;
    private String appName;
    private String orgName;
    private String orgId;
    private String spaceName;
    private String spaceId;
    private String serviceInstanceName;
    private String appGuid;
    private String serviceInstanceGuid;
    private List<Catalog> servicePlanList;
    private int limitSize;
    private String hostName;
    private String domainName;
    private String domainId;
    private String routeName;
    private String appSampleStartYn;
    private String appSampleFileName;
    private String appSampleFilePath;
    private int appSampleFileSize;
    private String appBindYn;
    private String parameter;
    private String app_bind_parameter;
    private int diskSize;
    private int memorySize;
    private String dashboardUseYn;
    private String onDemandYn;

}
