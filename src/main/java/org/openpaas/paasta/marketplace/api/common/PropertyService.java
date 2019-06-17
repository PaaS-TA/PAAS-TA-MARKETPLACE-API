package org.openpaas.paasta.marketplace.api.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Data
public class PropertyService {

	// PaaS-TA CF
    @Value("${cf.java.client.api.uri}")
    private String cfJavaClientApiUri;

    @Value("${cf.java.client.api.authorization.username}")
    private String cfJavaClientApiUsername;

    @Value("${cf.java.client.api.authorization.password}")
    private String cfJavaClientApiPassword;

}
