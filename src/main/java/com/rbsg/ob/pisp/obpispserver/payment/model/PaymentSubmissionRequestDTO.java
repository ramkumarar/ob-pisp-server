package com.rbsg.ob.pisp.obpispserver.payment.model;

import javax.validation.constraints.NotNull;

public class PaymentSubmissionRequestDTO {
    @NotNull
    private String paymentId;
    @NotNull
    private String oauthCode;


    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOauthCode() {
        return oauthCode;
    }

    public void setOauthCode(String oauthCode) {
        this.oauthCode = oauthCode;
    }
}
