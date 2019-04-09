package org.openpaas.paasta.marketplace.api.config.cloudfoundry;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import reactor.core.publisher.Mono;

/**
 * Cloud Foundry API Token Provider
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-03-14
 */
public class TokenGrantProvider implements TokenProvider {
    private String token;

    public TokenGrantProvider(String token) {
        this.token = token;
    }

    @Override
    public Mono<String> getToken(ConnectionContext connectionContext) {
        return Mono.just(token);
    }
}
