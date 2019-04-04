package org.openpaas.paasta.marketplace.api.service;

import java.util.List;

import org.openpaas.paasta.marketplace.api.thirdparty.paas.Application;
import org.openpaas.paasta.marketplace.api.thirdparty.paas.ServiceInstance;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class PaasApplicationService extends AbstractService {

//	@Value("${kepri.paas.api.space}")
    private String kepriPaasSpaceGuid;
    
    @SuppressWarnings("unchecked")
    public List<Application> getUserAppList() {
        return paasUserRest.getForObject("/apps/all", List.class);
    }
    
    public Application getUserApp(String appGuid) {
    	return paasUserRest.getForObject("/apps/" + appGuid +"/stats?depth=2", Application.class);
    }

    @SuppressWarnings("unchecked")
    public List<Application> getSaasAppList() {
        return paasManagerRest.getForObject("/apps/all", List.class);
    }
    
    public Application createPaasApplication(Application application) {
    	MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
    	parts.add("file", new FileSystemResource(application.getFile()));
    	String url = "/apps/push/file?organizationGuid=" + application.getOrganizationGuid() + "&spaceGuid=" + application.getSpaceGuid() + "&pushType=" + application.getPushType()
    					+ "&buildpack=" + application.getBuildpack() + "&appName=" + application.getAppName() + "&hostName=" + application.getHostName() + "&instances=" + application.getInstances()
    					+ "&memory=" + application.getMemory() + "&diskQuota=" + application.getDiskQuota() + "&withStart=false";
    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    	HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(parts, headers);
    	return paasUserRest.postForObject(url, requestEntity, Application.class);
    }
    
    public void createService(String appGuid, String appName, List<String> planGuidList) {
		ServiceInstance requestIstance = new ServiceInstance(appGuid);
		requestIstance.setName(appName);
		requestIstance.setSpaceGuid(kepriPaasSpaceGuid);
		
		planGuidList.forEach(planGuid -> {
			requestIstance.setServicePlanGuid(planGuid);
			paasUserRest.postForObject("/service_instances", requestIstance, Object.class);	
		});		
	}
    
    public void updateAppState(String appGuid, String state) {   
    	paasUserRest.put("/apps/" + appGuid + "/state/" + state, null);
    }

}
