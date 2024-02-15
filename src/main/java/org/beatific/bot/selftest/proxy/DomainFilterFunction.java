package org.beatific.bot.selftest.proxy;

import java.net.URI;
import java.util.List;
import java.util.Set;

import org.springframework.boot.actuate.web.exchanges.HttpExchange.Request;
import org.springframework.boot.actuate.web.exchanges.HttpExchange.Response;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycleValidator;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.ResponseData;
import org.springframework.cloud.client.loadbalancer.reactive.ExchangeFilterFunctionUtils;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class DomainFilterFunction implements LoadBalancedExchangeFilterFunction{


    // private DomainIPResolver domainIPResolver;
    private static final String PERCENTAGE_SIGN = "%";
    
    // public DomainFilterFunction(DomainIPResolver domainIPResolver) {
	// 	this.domainIPResolver = domainIPResolver;
	// }

	private String host;
	private String scheme;
	private int port;

    @Override
	public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction next) {

		URI originalUrl = clientRequest.url();
		String domain = originalUrl.getHost();
		if (domain == null) {
			String message = String.format("Request URI does not contain a valid hostname: %s", originalUrl);
            log.warn(message);
			return Mono.just(ClientResponse.create(HttpStatus.BAD_REQUEST).body(message).build());
		}
		ExchangeFilterFunctionUtils.class

        ClientRequest newRequest = buildClientRequest(clientRequest, host, scheme, port);

        return next.exchange(newRequest);
	}
	

    static ClientRequest buildClientRequest(ClientRequest request, String host, String scheme, int port) {
		URI originalUrl = request.url();
        
		ClientRequest clientRequest = ClientRequest
				.create(request.method(), reconstructURI(host, scheme, port, originalUrl))
				.headers(headers -> headers.addAll(request.headers())).cookies(cookies -> {
					cookies.addAll(request.cookies());
				}).attributes(attributes -> attributes.putAll(request.attributes())).body(request.body()).build();
		
		return clientRequest;
	}

    private static URI reconstructURI(String host, String scheme, int port, URI original) {
		boolean encoded = containsEncodedParts(original);
		return UriComponentsBuilder.fromUri(original).scheme(scheme).host(host).port(port).build(encoded).toUri();
	}

    private static boolean containsEncodedParts(URI uri) {
		boolean encoded = (uri.getRawQuery() != null && uri.getRawQuery().contains(PERCENTAGE_SIGN))
				|| (uri.getRawPath() != null && uri.getRawPath().contains(PERCENTAGE_SIGN))
				|| (uri.getRawFragment() != null && uri.getRawFragment().contains(PERCENTAGE_SIGN));
		// Verify if it is really fully encoded. Treat partial encoded as unencoded.
		if (encoded) {
			try {
				UriComponentsBuilder.fromUri(uri).build(true);
				return true;
			}
			catch (IllegalArgumentException ignore) {
			}
			return false;
		}
		return false;
	}

	ClientRequest buildClientRequest(ClientRequest request, ServiceInstance serviceInstance,
			String instanceIdCookieName, boolean addServiceInstanceCookie,
			List<LoadBalancerClientRequestTransformer> transformers) {
		URI originalUrl = request.url();
		ClientRequest clientRequest = ClientRequest
				.create(request.method(), LoadBalancerUriTools.reconstructURI(serviceInstance, originalUrl))
				.headers(headers -> headers.addAll(request.headers())).cookies(cookies -> {
					cookies.addAll(request.cookies());
					if (!(instanceIdCookieName == null || instanceIdCookieName.length() == 0)
							&& addServiceInstanceCookie) {
						cookies.add(instanceIdCookieName, serviceInstance.getInstanceId());
					}
				}).attributes(attributes -> attributes.putAll(request.attributes())).body(request.body()).build();
		if (transformers != null) {
			for (LoadBalancerClientRequestTransformer transformer : transformers) {
				clientRequest = transformer.transformRequest(clientRequest, serviceInstance);
			}
		}
		return clientRequest;
	}
    
}
