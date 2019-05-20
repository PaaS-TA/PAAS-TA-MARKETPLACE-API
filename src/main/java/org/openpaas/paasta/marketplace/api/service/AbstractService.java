package org.openpaas.paasta.marketplace.api.service;

import org.springframework.web.client.RestTemplate;

public abstract class AbstractService {

//	@Resource(name = "hubpopRest")
    protected RestTemplate hubpopRest;

//    @Resource(name = "commRest")
    protected RestTemplate commRest;

//    @Resource(name = "paasUserRest")
    protected RestTemplate paasUserRest;

//    @Resource(name = "paasManagerRest")
    protected RestTemplate paasManagerRest;

}
