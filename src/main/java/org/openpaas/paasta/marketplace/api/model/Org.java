package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Data
public class Org {
    private Map metadata;
    private Map entity;

    private String name;
    private String newOrgName;
    private boolean recursive = true;

    private UUID guid;
    private String status;
    private int memoryUsage;
    private int memoryLimit;


    private List<Space> spaces = new ArrayList<Space>();

    private boolean billingEnabled = false;

    private Quota quota;
    private String quotaGuid;
    private String userId;

}
