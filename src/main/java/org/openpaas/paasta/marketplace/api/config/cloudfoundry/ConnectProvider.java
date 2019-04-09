package org.openpaas.paasta.marketplace.api.config.cloudfoundry;

import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.openpaas.paasta.marketplace.api.common.Common;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cloud Foundry API connection
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-03-14
 */
@Configuration
public class ConnectProvider {

    @Bean
    DefaultConnectionContext connectionContext(@Value("${cloudfoundry.api.url}") String apiHost, @Value("${cloudfoundry.api.sslSkipValidation}") Boolean sslSkipValidation) {
        return Common.createConnectionContext(apiHost, sslSkipValidation);
    }

    @Bean
    PasswordGrantTokenProvider tokenProvider(@Value("${cloudfoundry.user.admin.username}") String username,
                                             @Value("${cloudfoundry.user.admin.password}") String password) {
        return PasswordGrantTokenProvider.builder()
                .password(password)
                .username(username)
                .build();
    }
}
