package org.openpaas.paasta.marketplace.api.config.common;

import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.openpaas.paasta.marketplace.api.config.cloudfoundry.TokenGrantTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class Common {

    private static final Logger LOGGER = LoggerFactory.getLogger(Common.class);

    @Value("${market.org.name}")
    public String marketOrgName;

    @Value("${market.org..guid}")
    public String marketOrgGuid;

    @Value("${market.space.name}")
    public String marketSpaceName;

    @Value("${market.space.guid}")
    public String marketSpaceGuid;

    @Value("${market.quota_guid}")
    public String marketQuotaGuid;

    @Value("${cloudfoundry.cc.api.url}")
    public String apiTarget;

    @Value("${cloudfoundry.cc.api.proxyUrl}")
    public String proxyUrl;

    @Value("${cloudfoundry.cc.api.uaaUrl}")
    public String uaaTarget;

    @Value("${cloudfoundry.cc.api.sslSkipValidation}")
    public boolean cfskipSSLValidation;

    @Value("${cloudfoundry.user.admin.username}")
    public String adminUserName;

    @Value("${cloudfoundry.user.admin.password}")
    public String adminPassword;

    public static final String AUTHORIZATION_HEADER_KEY = "cf-Authorization";

    @Value("${cloudfoundry.user.uaaClient.clientId}")
    public String uaaClientId;

    @Value("${cloudfoundry.user.uaaClient.clientSecret}")
    public String uaaClientSecret;

    @Value("${cloudfoundry.user.uaaClient.adminClientId}")
    public String uaaAdminClientId;

    @Value("${cloudfoundry.user.uaaClient.adminClientSecret}")
    public String uaaAdminClientSecret;

    @Value("${cloudfoundry.user.uaaClient.skipSSLValidation}")
    public boolean uaaskipSSLValidation;

    private Long currentTimeMiils = System.currentTimeMillis();


    @Autowired
    ConnectionContext connectionContext;

    @Autowired
    PasswordGrantTokenProvider tokenProvider;


    public ObjectMapper objectMapper = new ObjectMapper();

    /**
     * DefaultCloudFoundryOperations을 생성하여, 반환한다.
     *
     * @param connectionContext
     * @param tokenProvider
     * @return DefaultCloudFoundryOperations
     */
    public DefaultCloudFoundryOperations cloudFoundryOperations(org.cloudfoundry.reactor.ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return DefaultCloudFoundryOperations.builder().cloudFoundryClient(cloudFoundryClient(connectionContext, tokenProvider)).dopplerClient(dopplerClient(connectionContext, tokenProvider)).uaaClient(uaaClient(connectionContext, tokenProvider)).build();
    }

    public DefaultCloudFoundryOperations cloudFoundryOperations(TokenProvider tokenProvider) {
        return DefaultCloudFoundryOperations.builder().cloudFoundryClient(cloudFoundryClient(connectionContext(), tokenProvider)).dopplerClient(dopplerClient(connectionContext(), tokenProvider)).uaaClient(uaaClient(connectionContext(), tokenProvider)).build();
    }

    public DefaultCloudFoundryOperations cloudFoundryOperations() {
        return DefaultCloudFoundryOperations.builder().cloudFoundryClient(cloudFoundryClient(connectionContext(), tokenProvider())).dopplerClient(dopplerClient(connectionContext(), tokenProvider())).uaaClient(uaaClient(connectionContext(), tokenProvider)).build();
    }

    /**
     * ReactorCloudFoundryClient 생성하여, 반환한다.
     *
     * @param connectionContext
     * @param tokenProvider
     * @return DefaultCloudFoundryOperations
     */
    public ReactorCloudFoundryClient cloudFoundryClient(org.cloudfoundry.reactor.ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder().connectionContext(connectionContext).tokenProvider(tokenProvider).build();
    }

    public ReactorCloudFoundryClient cloudFoundryClient(TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider).build();
    }

    public ReactorCloudFoundryClient cloudFoundryClient() {
        return ReactorCloudFoundryClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider()).build();
    }

    /**
     * ReactorDopplerClient 생성하여, 반환한다.
     *
     * @param connectionContext
     * @param tokenProvider
     * @return ReactorDopplerClient
     */
    public ReactorDopplerClient dopplerClient(org.cloudfoundry.reactor.ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorDopplerClient.builder().connectionContext(connectionContext).tokenProvider(tokenProvider).build();
    }

    public ReactorDopplerClient dopplerClient(TokenProvider tokenProvider) {
        return ReactorDopplerClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider).build();
    }

    public ReactorDopplerClient dopplerClient() {
        return ReactorDopplerClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider()).build();
    }

    /**
     * ReactorUaaClient 생성하여, 반환한다.
     *
     * @param connectionContext
     * @param tokenProvider
     * @return ReactorUaaClient
     */
    public ReactorUaaClient uaaClient(org.cloudfoundry.reactor.ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorUaaClient.builder().connectionContext(connectionContext).tokenProvider(tokenProvider).build();
    }

    public ReactorUaaClient uaaClient(TokenProvider tokenProvider) {
        return ReactorUaaClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider).build();
    }

    public ReactorUaaClient uaaClient() {
        return ReactorUaaClient.builder().connectionContext(connectionContext()).tokenProvider(tokenProvider()).build();
    }


    /**
     * ReactorUaaClient(관리자) 생성하여, 반환한다.
     *
     * @param connectionContext
     * @param uaaAdminClientId
     * @param uaaAdminClientSecret
     * @return ReactorUaaClient
     */
    public ReactorUaaClient uaaAdminClient(org.cloudfoundry.reactor.ConnectionContext connectionContext, String adminUserName, String adminPassword, String uaaAdminClientId, String uaaAdminClientSecret) {
        ReactorUaaClient reactorUaaClient = uaaClient(connectionContext, tokenProvider);
        GetTokenByClientCredentialsResponse getTokenByClientCredentialsResponse = reactorUaaClient.tokens().getByClientCredentials(GetTokenByClientCredentialsRequest.builder().clientId(uaaAdminClientId).clientSecret(uaaAdminClientSecret).build()).block();
        return uaaClient(connectionContext, clinetTokenProvider(getTokenByClientCredentialsResponse.getAccessToken()));
    }

    public DefaultConnectionContext defaultConnectionContextBuild(String cfApiUrl,String proxyUrl, boolean cfskipSSLValidation) {
        return DefaultConnectionContext.builder().apiHost(cfApiUrl.replace("https://", "").replace("http://", "")).skipSslValidation(cfskipSSLValidation).keepAlive(true).build();
    }

    /**
     * DefaultConnectionContext 가져온다.
     *
     * @return DefaultConnectionContext
     */
    public DefaultConnectionContext connectionContext() {


        if (connectionContext == null) {
            connectionContext = new ConnectionContext(defaultConnectionContextBuild(apiTarget,proxyUrl,cfskipSSLValidation), new Date());
        } else {
            if (connectionContext.getCreate_time() != null) {
                Calendar now = Calendar.getInstance();
                Calendar create_time = Calendar.getInstance();
                create_time.setTime(connectionContext.getCreate_time());
                create_time.add(Calendar.MINUTE, 5);
                if (create_time.getTimeInMillis() < now.getTimeInMillis()) {
                    LOGGER.info("create_time.getTimeInMillis() :::: " + create_time.getTimeInMillis() + " ,  now.getTimeInMillis() ::::   " + now.getTimeInMillis());
                    tokenProvider = null;
                    connectionContext.getConnectionContext().dispose();
                    connectionContext = null;
                    connectionContext = new ConnectionContext(defaultConnectionContextBuild(apiTarget,proxyUrl,cfskipSSLValidation), new Date());

                }
            } else {
                connectionContext = null;
                connectionContext = new ConnectionContext(defaultConnectionContextBuild(apiTarget,proxyUrl,cfskipSSLValidation), new Date());
            }
        }
        return connectionContext.getConnectionContext();
    }


    /**
     * TokenGrantTokenProvider 생성하여, 반환한다.
     *
     * @param token
     * @return DefaultConnectionContext
     * @throws Exception
     */
    public TokenProvider tokenProvider(String token) {

        if (token.indexOf("bearer") < 0) {
            token = "bearer " + token;
        }
        TokenProvider tokenProvider = new TokenGrantTokenProvider(token);
        String name = uaaClient(connectionContext(), tokenProvider).getUsername().block();

        if (name.equals("admin")) {
            return tokenProvider();
        }

        return tokenProvider;

    }

    public TokenGrantTokenProvider clinetTokenProvider(String token) {

        if (token.indexOf("bearer") < 0) {
            token = "bearer " + token;
        }
        TokenGrantTokenProvider tokenProvider = new TokenGrantTokenProvider(token);
        return tokenProvider;

    }


    public PasswordGrantTokenProvider tokenProvider(String username, String password) {
        return PasswordGrantTokenProvider.builder().password(password).username(username).build();
    }

    public PasswordGrantTokenProvider tokenProvider() {
        if (tokenProvider == null) {
            tokenProvider = PasswordGrantTokenProvider.builder().password(adminPassword).username(adminUserName).build();
        }
        return tokenProvider;
    }


    /******************************************************************************* Utill/
     /**
     * 요청 파라미터들의 빈값 또는 null값 확인을 하나의 메소드로 처리할 수 있도록 생성한 메소드
     * 요청 파라미터 중 빈값 또는 null값인 파라미터가 있는 경우, false를 리턴한다.
     *
     * @param params
     * @return boolean
     */
    public boolean stringNullCheck(String... params) {
        return Arrays.stream(params).allMatch(param -> null != param && !param.equals(""));
    }

}
