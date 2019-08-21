package org.openpaas.paasta.marketplace.api.config.cloudfoundry;

import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import reactor.core.publisher.Mono;


public class TokenGrantTokenProvider implements TokenProvider{

    private String token;

    public TokenGrantTokenProvider(String token) {
        this.token = token;
    }

    @Override
    public Mono<String> getToken(ConnectionContext connectionContext) {

        return Mono.just(token);
    }
}
