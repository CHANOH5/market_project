package com.cs.market.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class PaymentWebClientConfig {

    public WebClient paymentWebClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .responseTimeout(Duration.ofSeconds(2))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(3))
                             .addHandlerLast(new WriteTimeoutHandler(3))
                );

        return WebClient.builder()
                .baseUrl("https://allra-pay.beeceptor.com")         // beeceptor 모의 API 주소
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    } // paymentWebClient

} // end class
