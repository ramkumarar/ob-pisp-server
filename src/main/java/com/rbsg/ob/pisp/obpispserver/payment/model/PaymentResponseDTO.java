package com.rbsg.ob.pisp.obpispserver.payment.model;

import javax.validation.constraints.NotNull;

public class PaymentResponseDTO {

    @NotNull
    private String endToEndIdentifier;

    @NotNull
    private String paymentId;

    @NotNull
    private String status;

    private String amount;

    private String currency;

    public String getEndToEndIdentifier() {
        return endToEndIdentifier;
    }

    public void setEndToEndIdentifier(String endToEndIdentifier) {
        this.endToEndIdentifier = endToEndIdentifier;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
