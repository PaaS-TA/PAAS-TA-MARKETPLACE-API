package org.openpaas.paasta.marketplace.api.storageApi.root;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
public class RootController {


    //////////////////////////////////////////////////////////////////////
    //////   * CLOUD FOUNDRY CLIENT API VERSION 2                   //////
    //////   Document : http://apidocs.cloudfoundry.org             //////
    //////////////////////////////////////////////////////////////////////
    @Value("${objectStorage.swift.tenantName}")
    String tenantName;
    @Value("${objectStorage.swift.username}")
    String username;
    @Value("${objectStorage.swift.authUrl}")
    String authUrl;

    @Value("${objectStorage.swift.authMethod}")
    String authMethod;
    @Value("${objectStorage.swift.container}")
    String container;
    @Value("${objectStorage.swift.preferredRegion}")
    String preferredRegion;


    @GetMapping("/swift/info")
    public Map index() throws Exception {

        Map map = new HashMap();

        Map objectStorage = new HashMap();
        objectStorage.put("username", authUrl);
        objectStorage.put("tenantName", tenantName);
        objectStorage.put("username", username);
        objectStorage.put("authMethod", authMethod);
        objectStorage.put("container", container);
        objectStorage.put("preferredRegion", preferredRegion);


        map.put("objectStorage", objectStorage);

        map.put("name", "PaaS-TA Storage API");
        return map;
    }

}
