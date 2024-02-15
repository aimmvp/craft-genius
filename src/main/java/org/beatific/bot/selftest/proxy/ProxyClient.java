package org.beatific.bot.selftest.proxy;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.net.ssl.SSLException;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
public class ProxyClient {

    private final WebClient webclient;

    public ProxyClient(String host, String scheme, int port, String domain, int connectionTimeout, int readTimeout) {

        try {
            SslContext context = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();

            HttpClient httpclient = HttpClient.create().secure(t -> t.sslContext(context))
            // HttpClient httpclient = HttpClient.create()
                    // .proxy(it -> it.type(Proxy.HTTP).host(ip).port(port))
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout)
                    // .responseTimeout(Duration.ofMillis(responseTimeout))
                    .doOnConnected(
                            conn -> conn.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS))
                    // .addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS))
                    );
            webclient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpclient)).baseUrl(domain)
                    .filters(exchangeFilterFunctions -> {
                        exchangeFilterFunctions.add(logRequest());
                        exchangeFilterFunctions.add(logResponse());
                        // exchangeFilterFunctions.add(new DomainFilterFunction(host, scheme, port));
                        exchangeFilterFunctions.add(new DomainFilter(host, scheme, port));
                    }).build();

        } catch (SSLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }

    ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response: ");
            clientResponse
                    .headers().asHttpHeaders()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            clientResponse
                    .bodyToMono(String.class).subscribe(body -> log.info(body));
            return Mono.just(clientResponse);
        });
    }

    public void get(String uri, Consumer<String> resultFunction) {
        String body = webclient.get().uri(uri).retrieve()
                // .onStatus(HttpStatus::is4xxClientError, response -> response.)
                // .onStatus(HttpStatus::is5xxServerError, response -> ...)
                // .onStatus(status -> status.is2xxSuccessful(), response -> {
                // log.error("success"); return Mono.error(new RuntimeException("hi"));})
                .bodyToMono(String.class)
                .block();
        log.error("body : {}", body);

        // .subscribe(str -> resultFunction.accept(str));
        // .subscribe(System.err::println);
        // webclient.get().uri(uri).exchangeToMono(response -> extracted(response));

        // Mono<String> mono = WebClient.create()
        // .get() // or post(), put(), delete() ...
        // .uri("https://ifconfig.me")
        // .retrieve()
        // .bodyToMono(String.class);
        // // System.out.println(mono.block());
        // mono.subscribe(System.out::println);

    }

    // private Mono<Object> extracted(ClientResponse response) {

    // log.info("response.statusCode {}", response.statusCode());
    // if (response.statusCode().equals(HttpStatus.OK)) {
    // return response.bodyToMono(String.class);
    // }
    // else if (response.statusCode().is4xxClientError()) {
    // return response.bodyToMono(String.class);
    // }

    // return Mono.just(null);

    // }

    public <T> void getbyType(String uri, Class<T> clazz, Consumer<T> resultFunction) {
        webclient.get().uri(uri).retrieve().bodyToMono(clazz).subscribe(result -> resultFunction.accept(result));
    }
}
