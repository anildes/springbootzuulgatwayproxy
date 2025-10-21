package com.example.springbootzuulgatwayproxystudentservice.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;

/**
 * Intercepts outbound requests from the Zuul Gateway's RestTemplate.
 * This class implements the Client Credentials Grant flow:
 * 1. Checks if a valid Access Token is available in the cache.
 * 2. If the token is expired, it fetches a new one from the OAuth2 Authorization Server (e.g., Google).
 * 3. Adds the token as an "Authorization: Bearer" header to the downstream request.
 */
@Component
public class ZuulAuthInterceptor implements ClientHttpRequestInterceptor {

    // --- Injected Properties (from application.properties) ---
    private final String clientId;
    private final String clientSecret;
    private final String tokenUri;
    private final String scope;

    // --- State Variables for Token Caching ---
    // volatile ensures changes are visible across threads
    private volatile String accessToken = null;
    private volatile Instant expiresAt = Instant.MIN;

    // RestTemplate dedicated to fetching the token
    private final RestTemplate tokenRestTemplate = new RestTemplate();

    public ZuulAuthInterceptor(
            @Value("${spring.security.oauth2.client.registration.my-client.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.my-client.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.client.provider.my-client.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.registration.my-client.scope}") String scope) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUri = tokenUri;
        this.scope = scope;
        System.out.println("ZuulAuthInterceptor initialized with clientId: " + clientId + ", tokenUri: " + tokenUri);
    }

    /**
     * Intercepts the request to the downstream service and injects the access token.
     */
    

    /**
     * Retrieves the cached access token, refreshing it if it's expired or near expiration.
     */
    private String getAccessToken() {
        // Use a 60-second buffer to refresh the token slightly early
        if (accessToken == null || Instant.now().isAfter(expiresAt.minusSeconds(60))) {
            // Synchronize to ensure only one thread attempts to fetch a new token
            synchronized (this) {
                // Double-check lock: check again after acquiring the lock
                if (accessToken == null || Instant.now().isAfter(expiresAt.minusSeconds(60))) {
                    fetchToken();
                }
            }
        }
        return accessToken;
    }

    /**
     * Performs the actual OAuth 2.0 Client Credentials Grant request to the token endpoint.
     */
    private void fetchToken() {
        // 1. Prepare Credentials for HTTP Basic Auth
        // The format is: Base64(client_id:client_secret)
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        // 2. Set Headers for the Token Request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        // Set the Authorization Header for Basic Authentication
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth); 

        // 3. Set Body Parameters for the Token Request
        // **CRITICAL CHANGE:** Remove client_id and client_secret from the body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "Client_Credentials");
        //body.add("grant_type", "Authorization_Code");
        body.add("scope", scope); 

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            // 4. Send the Token Request
            ResponseEntity<TokenResponse> response = tokenRestTemplate.exchange(
                    tokenUri,
                    HttpMethod.POST,
                    entity,
                    TokenResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                TokenResponse tokenResponse = response.getBody();
                this.accessToken = tokenResponse.getAccess_token();
                this.expiresAt = Instant.now().plusSeconds(tokenResponse.getExpires_in() - 10);
            } else {
                System.err.println("Error fetching token: " + response.getStatusCode());
                this.accessToken = null;
            }
        } catch (Exception e) {
            System.err.println("Exception during token fetch: " + e.getMessage());
            this.accessToken = null;
        }
    }

    /**
     * Inner Class to map the JSON response from the Token Endpoint.
     * Note: Variable names must match the snake_case used in the OAuth2 response.
     */
    private static class TokenResponse {
        private String access_token;
        private long expires_in;

        // Getters and Setters are required for Jackson deserialization
        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public long getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(long expires_in) {
            this.expires_in = expires_in;
        }
    }

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}