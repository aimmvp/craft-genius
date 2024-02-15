package org.beatific.bot.selftest.proxy;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.CompletionContext;
import org.springframework.cloud.client.loadbalancer.DefaultRequest;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle;
import org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycleValidator;
import org.springframework.cloud.client.loadbalancer.LoadBalancerProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestData;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.ResponseData;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import com.gargoylesoftware.htmlunit.javascript.host.URL;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@SuppressWarnings({ "rawtypes", "unchecked" })
@AllArgsConstructor
public class DomainFilter implements LoadBalancedExchangeFilterFunction {

	private String host;
	private String scheme;
	private int port;

	@Override
	public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction next) {
		
		return choose(clientRequest.url()).flatMap(instance -> {

			ClientRequest newRequest = buildClientRequest(clientRequest, instance);
			return next.exchange(newRequest);
		});
	}

	ClientRequest buildClientRequest(ClientRequest request, ServiceInstance serviceInstance) {
		URI originalUrl = request.url();
		ClientRequest clientRequest = ClientRequest
				.create(request.method(), LoadBalancerUriTools.reconstructURI(serviceInstance, originalUrl))
				.headers(headers -> headers.addAll(request.headers())).cookies(cookies -> {
					cookies.addAll(request.cookies());
				}).attributes(attributes -> attributes.putAll(request.attributes())).body(request.body()).build();
		// if (transformers != null) {
		// 	for (LoadBalancerClientRequestTransformer transformer : transformers) {
		// 		clientRequest = transformer.transformRequest(clientRequest, serviceInstance);
		// 	}
		// }
		// log.info("clientRequest[{}]",clientRequest);
		return clientRequest;
	}

	protected Mono<ServiceInstance> choose(URI uri) {
		return Mono.just(new ServiceInstance() {

			@Override
			public String getServiceId() {
				return  host;
			}

			@Override
			public String getHost() {
				return host;
			}

			@Override
			public int getPort() {
				return port;
			}

			@Override
			public boolean isSecure() {
				return "https".equals(uri.getScheme());
			}

			@Override
			public URI getUri() {
				return uri;
			}

			@Override
			public Map<String, String> getMetadata() {
				return new HashMap<>();
			}

		});
	}
}
