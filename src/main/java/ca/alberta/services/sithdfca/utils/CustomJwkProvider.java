package ca.alberta.services.sithdfca.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.SigningKeyNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import lombok.extern.slf4j.Slf4j;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Slf4j

public class CustomJwkProvider implements JwkProvider {

	private final URI uri;
	private final ObjectReader reader;
	private static HashMap<String, Object> cachedPublicJwks = null;

	public CustomJwkProvider(String jwkProviderUrl) {
		this(jwkProviderUrl,false);
	}
	public CustomJwkProvider(String jwkProviderUrl, boolean flushCache) {
		if(flushCache && cachedPublicJwks != null)
			cachedPublicJwks.clear();
		
		try {
			this.uri = new URI(jwkProviderUrl).normalize();
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("Invalid jwks uri", e);
		}
		this.reader = new ObjectMapper().readerFor(Map.class);
	}

	@Override
	public Jwk get(String keyId) throws JwkException
	{
		final List<Jwk> jwks = getAll();
		if (keyId == null && jwks.size() == 1) {
			return jwks.get(0);
		}
		if (keyId != null) {
			for (Jwk jwk : jwks) {
				if (keyId.equals(jwk.getId())) {
					return jwk;
				}
			}
		}
		throw new SigningKeyNotFoundException("No key found in " + uri.toString() + " with kid " + keyId, null);
	}

	@SuppressWarnings("unchecked")
	private List<Jwk> getAll() throws SigningKeyNotFoundException {
		
		List<Jwk> jwks = new ArrayList<Jwk>();
		final List<Map<String, Object>> keys = (List<Map<String, Object>>) getJwks().get("keys");

		if (keys == null || keys.isEmpty()) {
			throw new SigningKeyNotFoundException("No keys found in " + uri.toString(), null);
		}

		try {
			for (Map<String, Object> values : keys) {
				jwks.add(Jwk.fromValues(values));
			}
		} catch (IllegalArgumentException e) {
			throw new SigningKeyNotFoundException("Failed to parse jwk from json", e);
		}
		return jwks;
	}

	private Map<String, Object> getJwks() throws SigningKeyNotFoundException {

		if(cachedPublicJwks!= null && cachedPublicJwks.size() > 0) {
			if(log.isDebugEnabled()) log.debug("--------------------- Using cached JWKS values --------------------");
			return cachedPublicJwks;
		} else {

			if(log.isDebugEnabled()) log.debug("--------------------- No cached JWKS, retrieving and caching --------------------");
			RestTemplate rt = null;
			try {
				rt = getRestTemplate();
			} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			ResponseEntity<String> response = rt.getForEntity(uri, String.class);
			
			try {
				cachedPublicJwks = reader.readValue(response.getBody());
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return cachedPublicJwks;
		}
	}
	
//	public static void main(String[] args) {
//		RestTemplate rt = null;
//		String uri = "https://vnl1213.gov.ab.ca:8443/auth/realms/TestRealm/protocol/openid-connect/certs";
//		try {
//			rt = new KeyCloakJwkProvider(uri).getRestTemplate();
//		} catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		ResponseEntity<String> response = rt.getForEntity(uri, String.class);
//		System.err.println(response.getBody());
//	}

	public RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	    TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
	    SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
	    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
	    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
	    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
	    requestFactory.setHttpClient(httpClient);
	    RestTemplate restTemplate = new RestTemplate(requestFactory);
	    return restTemplate;
	}
}