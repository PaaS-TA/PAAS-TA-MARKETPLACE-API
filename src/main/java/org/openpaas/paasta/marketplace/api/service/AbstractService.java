package org.openpaas.paasta.marketplace.api.service;

import org.openpaas.paasta.marketplace.api.BaseComponent;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractService extends BaseComponent {

//	@Resource(name = "hubpopRest")
    protected RestTemplate hubpopRest;

//    @Resource(name = "commRest")
    protected RestTemplate commRest;

//    @Resource(name = "paasUserRest")
    protected RestTemplate paasUserRest;

//    @Resource(name = "paasManagerRest")
    protected RestTemplate paasManagerRest;

}
