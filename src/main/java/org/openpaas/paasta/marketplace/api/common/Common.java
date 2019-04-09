package org.openpaas.paasta.marketplace.api.common;

import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.doppler.DopplerClient;
import org.cloudfoundry.operations.DefaultCloudFoundryOperations;
import org.cloudfoundry.reactor.ConnectionContext;
import org.cloudfoundry.reactor.DefaultConnectionContext;
import org.cloudfoundry.reactor.TokenProvider;
import org.cloudfoundry.reactor.client.ReactorCloudFoundryClient;
import org.cloudfoundry.reactor.doppler.ReactorDopplerClient;
import org.cloudfoundry.reactor.tokenprovider.PasswordGrantTokenProvider;
import org.cloudfoundry.reactor.uaa.ReactorUaaClient;
import org.cloudfoundry.uaa.UaaClient;
import org.cloudfoundry.uaa.clients.GetClientRequest;
import org.cloudfoundry.uaa.clients.GetClientResponse;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsRequest;
import org.cloudfoundry.uaa.tokens.GetTokenByClientCredentialsResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.openpaas.paasta.marketplace.api.config.cloudfoundry.TokenGrantProvider;
import org.openpaas.paasta.marketplace.api.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Properties 및 CloudFoundryClient, DopplerClient 및 UaaClient 인스턴스화
 *
 * @author hrjin
 * @version 1.0
 * @since 2019-03-14
 */
public class Common {
    private static final Logger LOGGER = LoggerFactory.getLogger(Common.class);

    @Value("${cloudfoundry.api.url}")
    public String cfApiUrl;

    @Value("${cloudfoundry.api.uaaUrl}")
    public String cfUaaUrl;

    @Value("${cloudfoundry.api.host}")
    public String cfApiHost;

    @Value("${cloudfoundry.user.admin.username}")
    public String cfAdminUserName;

    @Value("${cloudfoundry.user.admin.password}")
    public String cfAdminPassword;

    @Value("${cloudfoundry.api.sslSkipValidation}")
    public boolean skipSSLValidation;

    @Value("${cloudfoundry.user.uaaClient.clientId}")
    public String uaaClientId;

    @Value("${cloudfoundry.user.uaaClient.clientSecret}")
    public String uaaClientSecret;

    @Value("${cloudfoundry.user.uaaClient.adminClientId}")
    public String uaaAdminClientId;

    @Value("${cloudfoundry.user.uaaClient.adminClientSecret}")
    public String uaaAdminClientSecret;


    @Autowired
    public LoginService loginService;

    @Autowired
    DefaultConnectionContext connectionContext;

    @Autowired
    PasswordGrantTokenProvider tokenProvider;

    private static final ThreadLocal<DefaultConnectionContext> connectionContextThreadLocal = new ThreadLocal<>();

    private static ReactorCloudFoundryClient adminReactorCloudFoundryClient;

    public ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 관리자 토큰을 가져온다.
     *
     * @return String token
     * @throws Exception the exception
     */
    public String getToken() {
        try {
            return loginService.login(cfAdminUserName, cfAdminPassword).getValue();
        } catch (Exception e) {
            return null;
        }
    }

//    public String adminToken(){
//        try {
//            return loginService.login(cfAdminUserName, cfAdminPassword).getValue();
//        } catch (Exception e) {
//            return null;
//        }
//    }

    /**
     * CF Target URL을 가져온다.
     *
     * @param target cf target
     * @return URL target
     * @throws MalformedURLException
     * @throws URISyntaxException
     */
    public URL getTargetURL(String target) throws MalformedURLException, URISyntaxException {
        return getTargetURI(target).toURL();
    }

    /**
     * CF Target URI를 가져온다.
     *
     * @param target cf target
     * @return URI target
     * @throws URISyntaxException
     */
    private URI getTargetURI(String target) throws URISyntaxException {
        return new URI(target);
    }

    /**
     * ReactorCloudFoundryClient 생성하여, 반환한다.
     *
     * @param connectionContext the connectionContext
     * @param tokenProvider the tokenProvider
     * @return ReactorCloudFoundryClient
     */
    public static ReactorCloudFoundryClient cloudFoundryClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorCloudFoundryClient.builder().connectionContext(connectionContext).tokenProvider(tokenProvider).build();
    }

    /**
     * DefaultConnectionContext 가져온다.
     *
     * @return DefaultConnectionContext
     */
    public DefaultConnectionContext connectionContext() {
        return connectionContext;
    }

    /**
     * DefaultConnectionContext 생성하여, 반환한다.
     *
     * @param apiUrl
     * @param skipSSLValidation
     * @return DefaultConnectionContext
     */
    public static DefaultConnectionContext createConnectionContext(String apiUrl, boolean skipSSLValidation) {
        DefaultConnectionContext connectionContext = peekConnectionContext();
        if (null != connectionContext) {
            boolean isEqual = connectionContext.getApiHost().equals(convertApiUrl(apiUrl)) && connectionContext.getSkipSslValidation().get() == skipSSLValidation;
            if (!isEqual) {
                removeConnectionContext();
                connectionContext = null;
            }
        }

        if (null == connectionContext) {
            connectionContext = DefaultConnectionContext.builder().apiHost(convertApiUrl(apiUrl)).skipSslValidation(skipSSLValidation).keepAlive(true).build();
            pushConnectionContext(connectionContext);
        }

        return connectionContext;
    }

    /**
     * CF API https -> http 로 변환.
     *
     * @param url the url
     * @return String
     */
    public static String convertApiUrl(String url) {
        return url.replace("https://", "").replace("http://", "");
    }

    /**
     * ThreadLocal 내의 ConnectionContext 타입의 값을 조회한다.
     *
     * @return DefaultConnectionContext
     */
    private static DefaultConnectionContext peekConnectionContext() {
        return connectionContextThreadLocal.get();
    }

    /**
     * ThreadLocal 에 connectionContext 셋팅한다.
     *
     * @param connectionContext the connectionContext
     */
    private static void pushConnectionContext(DefaultConnectionContext connectionContext) {
        connectionContextThreadLocal.set(connectionContext);
        LOGGER.info("Create connection context and push thread local : DefalutConnectionContext@{}", Integer.toHexString(connectionContext.hashCode()));
    }

    /**
     * ThreadLocal 에 connectionContext 제거한다.
     *
     */
    private static void removeConnectionContext() {
        disposeConnectionContext(connectionContextThreadLocal.get());
        connectionContextThreadLocal.remove();
    }

    /**
     * ConnectionContext 연결 해제한다.
     *
     * @param connectionContext the connectionContext
     */
    private static void disposeConnectionContext(DefaultConnectionContext connectionContext) {
        try {
            if (null != connectionContext) connectionContext.dispose();
        } catch (Exception ignore) {
        }
    }

    /**
     * TokenGrantTokenProvider 생성하여, 반환한다.
     *
     * @param token String token
     * @return TokenGrantProvider
     * @throws Exception
     */
    public static TokenGrantProvider tokenProvider(String token) {
        try {
            if (token.indexOf("bearer") < 0) {
                token = "bearer " + token;
            }
            return new TokenGrantProvider(token);
        } catch (Exception e) {
            return null;
        }
    }

    public PasswordGrantTokenProvider tokenProvider() {
        return tokenProvider;
    }

    /**
     * token을 제공하는 클레스 사용자 임의의 clientId를 사용하며,
     * user token, client token을 모두 얻을 수 있다.
     *
     * @param username
     * @param password
     * @return PasswordGrantTokenProvider
     */
    public static PasswordGrantTokenProvider tokenProvider(String username, String password) {
        return PasswordGrantTokenProvider.builder().password(password).username(username).build();
    }

    /**
     * DefaultCloudFoundryOperations 를 생성하여, 반환한다.
     *
     * @param connectionContext the connectionContext
     * @param tokenProvider the tokenProvider
     * @return DefaultCloudFoundryOperations
     */
    public static DefaultCloudFoundryOperations cloudFoundryOperations(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return cloudFoundryOperations(cloudFoundryClient(connectionContext, tokenProvider), dopplerClient(connectionContext, tokenProvider), uaaClient(connectionContext, tokenProvider));
    }

    /**
     * DefaultCloudFoundryOperations을 생성하여, 반환한다.
     *
     * @param cloudFoundryClient the cloudFoundryClient
     * @param dopplerClient the dopplerClient
     * @param uaaClient the uaaClient
     * @return DefaultCloudFoundryOperations
     */
    public static DefaultCloudFoundryOperations cloudFoundryOperations(org.cloudfoundry.client.CloudFoundryClient cloudFoundryClient, DopplerClient dopplerClient, UaaClient uaaClient) {
        return DefaultCloudFoundryOperations.builder().cloudFoundryClient(cloudFoundryClient).dopplerClient(dopplerClient).uaaClient(uaaClient).build();
    }



    /**
     * ReactorDopplerClient 생성하여, 반환한다.
     *
     * @param connectionContext the connectionContext
     * @param tokenProvider the tokenProvider
     * @return ReactorDopplerClient
     */
    public static ReactorDopplerClient dopplerClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorDopplerClient.builder().connectionContext(connectionContext).tokenProvider(tokenProvider).build();
    }

    /**
     * ReactorUaaClient 생성하여, 반환한다.
     *
     * @param connectionContext the connectionContext
     * @param tokenProvider the tokenProvider
     * @return ReactorUaaClient
     */
    public static ReactorUaaClient uaaClient(ConnectionContext connectionContext, TokenProvider tokenProvider) {
        return ReactorUaaClient.builder().connectionContext(connectionContext).tokenProvider(tokenProvider).build();
    }





    /**
     * get CloudCredentials Object from token String
     *
     * @param token
     * @return CloudCredentials
     */
    public CloudCredentials getCloudCredentials(String token) {
        return new CloudCredentials(getOAuth2AccessToken(token), false);
    }

    /**
     * get DefailtOAuth2AccessToken Object from token String
     *
     * @param token
     * @return
     */
    private DefaultOAuth2AccessToken getOAuth2AccessToken(String token) {
        return new DefaultOAuth2AccessToken(token);
    }






    /**
     * ReactorUaaClient(관리자) 생성하여, 반환한다.
     *
     * @param connectionContext
     * @param apiTarget
     * @param token
     * @param uaaAdminClientId
     * @param uaaAdminClientSecret
     * @return ReactorUaaClient
     */
    public static ReactorUaaClient uaaAdminClient(ConnectionContext connectionContext, String apiTarget, String token, String uaaAdminClientId, String uaaAdminClientSecret) {
        ReactorUaaClient reactorUaaClient = Common.uaaClient(connectionContext, tokenProvider(token));
        GetTokenByClientCredentialsResponse getTokenByClientCredentialsResponse = reactorUaaClient.tokens().getByClientCredentials(GetTokenByClientCredentialsRequest.builder().clientId(uaaAdminClientId).clientSecret(uaaAdminClientSecret).build()).block();
        return Common.uaaClient(connectionContext, tokenProvider(getTokenByClientCredentialsResponse.getAccessToken()));
    }



    /**
     * credentials 세팅
     *
     * @param uaaClientId
     * @return ResourceOwnerPasswordResourceDetails
     */
    private ResourceOwnerPasswordResourceDetails getCredentials(String uaaClientId) {
        ResourceOwnerPasswordResourceDetails credentials = new ResourceOwnerPasswordResourceDetails();
        credentials.setAccessTokenUri(cfUaaUrl + "/oauth/token?grant_type=client_credentials&response_type=token");
        credentials.setClientAuthenticationScheme(AuthenticationScheme.header);

        credentials.setClientId(uaaClientId);

        if (uaaClientId.equals(uaaAdminClientId)) {
            credentials.setClientSecret(uaaAdminClientSecret);
        }
        return credentials;
    }



    public String adminToken(String token){
        try {
            String name = Common.uaaClient(connectionContext(), tokenProvider(token)).getUsername().block();
            if (name.equals("admin")) {
                return tokenProvider(cfAdminUserName, cfAdminPassword).getToken(connectionContext()).block();
            }
            return token;
        } catch (Exception e){
            return token;
        }
    }


    /**
     * 클라이언트 정보 조회
     *
     * @param clientId clientId
     * @return GetClientResponse
     * @throws Exception the exception
     */
    //@HystrixCommand(commandKey = "getClient")
    public GetClientResponse doAuthorization(String clientId) throws Exception {
        return Common.uaaAdminClient(connectionContext(), cfApiUrl, "eyJhbGciOiJSUzI1NiIsImprdSI6Imh0dHBzOi8vdWFhLjExNS42OC40Ni4xODgueGlwLmlvL3Rva2VuX2tleXMiLCJraWQiOiJrZXktMSIsInR5cCI6IkpXVCIsImN0eSI6bnVsbH0.eyJqdGkiOiJkYTNhYjNhZDNmYTU0MjNlOGU4OTFlYmViYWI0MzBlNyIsInN1YiI6IjY5MDFkZDcxLWY2OGMtNDYxOC1iMWFkLTM0NTc4ODdhMDFmMiIsInNjb3BlIjpbIm9wZW5pZCIsInJvdXRpbmcucm91dGVyX2dyb3Vwcy53cml0ZSIsInNjaW0ucmVhZCIsImNsb3VkX2NvbnRyb2xsZXIuYWRtaW4iLCJ1YWEudXNlciIsInJvdXRpbmcucm91dGVyX2dyb3Vwcy5yZWFkIiwiY2xvdWRfY29udHJvbGxlci5yZWFkIiwicGFzc3dvcmQud3JpdGUiLCJjbG91ZF9jb250cm9sbGVyLndyaXRlIiwibmV0d29yay5hZG1pbiIsImRvcHBsZXIuZmlyZWhvc2UiLCJzY2ltLndyaXRlIl0sImNsaWVudF9pZCI6ImNmIiwiY2lkIjoiY2YiLCJhenAiOiJjZiIsImdyYW50X3R5cGUiOiJwYXNzd29yZCIsInVzZXJfaWQiOiI2OTAxZGQ3MS1mNjhjLTQ2MTgtYjFhZC0zNDU3ODg3YTAxZjIiLCJvcmlnaW4iOiJ1YWEiLCJ1c2VyX25hbWUiOiJhZG1pbiIsImVtYWlsIjoiYWRtaW4iLCJhdXRoX3RpbWUiOjE1NTMwMDY1ODgsInJldl9zaWciOiJhOWRkOTNiMyIsImlhdCI6MTU1MzA0MjU0MSwiZXhwIjoxNTUzMDQzMTQxLCJpc3MiOiJodHRwczovL3VhYS4xMTUuNjguNDYuMTg4LnhpcC5pby9vYXV0aC90b2tlbiIsInppZCI6InVhYSIsImF1ZCI6WyJzY2ltIiwiY2xvdWRfY29udHJvbGxlciIsInBhc3N3b3JkIiwiY2YiLCJ1YWEiLCJvcGVuaWQiLCJkb3BwbGVyIiwicm91dGluZy5yb3V0ZXJfZ3JvdXBzIiwibmV0d29yayJdfQ.qdQqHkzp5xrag3CkKSlw9N7hZf9MQ6RiB-Aw-fGsdSoorh0Vs2mm4pvoBF9X6cO23nDe51N5-IEAHCfELycC5d43JboGJ3uCTYC0xD4VbB-A5TrtgeptOvLQoXE0QIucxEC26huqrvcjhsxjX2L1L_B1f__PFJw8keQUX2lF_rmtq_v30JWd1urvUZoTt80u4XP_35nrEjLUzboMq4QhHJC4dlKgAIfR3Q7AwgVUB1VAjOxvKS5jWp1-epPuLIpEHAgrNTGbvowQ9twGg299LGgjbOWS0Ew5gdnhUGNMHwnsbvBSF1aln3eKB5AstJmzsYHXy7A8WnyOXewbHVKzsQ", "admin", "admin-secret").clients().get(GetClientRequest.builder().clientId(clientId).build()).log().block();
    }
}
