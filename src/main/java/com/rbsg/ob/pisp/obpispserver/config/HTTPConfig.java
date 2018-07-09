package com.rbsg.ob.pisp.obpispserver.config;

import com.rbsg.ob.pisp.obpispserver.payment.model.HttpRequestHelper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@ConfigurationProperties
public class HTTPConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Bean
    RestTemplate restTemplate() {


        HttpClient httpClient = HttpClients.createDefault();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);

        return new RestTemplate(requestFactory);
    }

   @Bean
    HttpRequestHelper requestHelper(@Value("${payment.baseurl}") String baseUrl,@Value("${payment.authurl}") String authUrl){
        return new HttpRequestHelper(baseUrl,authUrl);
   }

}
