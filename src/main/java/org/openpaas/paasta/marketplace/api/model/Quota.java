package org.openpaas.paasta.marketplace.api.model;

import lombok.Data;

import java.util.Map;
import java.util.UUID;


@Data
public class Quota {

	private Map entity;
	private Map metadata;

	private String name;
	private boolean nonBasicServicesAllowed = false;
	private int totalServices;
	private int totalRoutes;
	private int memoryLimit;
	private int instanceMemoryLimit;

	// add
	private int appInstanceLimit;
	private int totalReservedRoutePorts;
	private UUID guid;  // Definition GUID(Organization or Space)
	private UUID orginazationGuid;
	private UUID spaceGuid;

	private String organizationName; // 정의 지정시 필요
}