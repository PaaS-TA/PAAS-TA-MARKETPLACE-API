package org.openpaas.paasta.marketplace.api.config.cloudfoundry;


import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.openpaas.paasta.marketplace.api.config.common.Common;
import org.openpaas.paasta.marketplace.api.config.common.ConnectionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class CloudFoundryConfigProvider {
    @Bean
    ConnectionContext connectionContext(@Value("${cloudfoundry.cc.api.url}") String apiTarget, @Value("${cloudfoundry.cc.api.sslSkipValidation}") Boolean sslSkipValidation, @Value("${cloudfoundry.cc.api.proxyUrl}") String proxyUrl) {
        Common common = new Common();
        return new ConnectionContext(common.defaultConnectionContextBuild(apiTarget, proxyUrl, sslSkipValidation), new Date());
    }

    @Bean
    PasswordGrantTokenProvider tokenProvider(@Value("${cloudfoundry.user.admin.username}") String username, @Value("${cloudfoundry.user.admin.password}") String password) {
        return PasswordGrantTokenProvider.builder().password(password).username(username).build();
    }
}
