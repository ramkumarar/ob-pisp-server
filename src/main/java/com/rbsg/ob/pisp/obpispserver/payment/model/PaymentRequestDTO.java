package com.rbsg.ob.pisp.obpispserver.payment.model;

import javax.validation.constraints.NotNull;

public class PaymentRequestDTO {
    @NotNull
    private String merchantId;
    @NotNull
    private String merchantCustomerId;
    @NotNull
    private String endToEndIdentifier;
    @NotNull
    private String amount;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantCustomerId() {
        return merchantCustomerId;
    }

    public void setMerchantCustomerId(String merchantCustomerId) {
        this.merchantCustomerId = merchantCustomerId;
    }

    public String getEndToEndIdentifier() {
        return endToEndIdentifier;
    }

    public void setEndToEndIdentifier(String endToEndIdentifier) {
        this.endToEndIdentifier = endToEndIdentifier;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
