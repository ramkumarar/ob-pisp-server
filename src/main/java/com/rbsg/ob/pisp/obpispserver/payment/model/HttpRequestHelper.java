package com.rbsg.ob.pisp.obpispserver.payment.model;

public class HttpRequestHelper {
    private String baseUrl;
    private String authUrl;

    public HttpRequestHelper(String baseUrl,String authUrl){
        this.baseUrl=baseUrl;
        this.authUrl=authUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }
}
