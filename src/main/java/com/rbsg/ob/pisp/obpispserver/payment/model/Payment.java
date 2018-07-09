package com.rbsg.ob.pisp.obpispserver.payment.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Payment {
    @Id
    @GeneratedValue
    private Long id;
    private String paymentId;
    private String merchantId;
    private String merchantCustomerId;
    private String endToEndIdentifier;
    private String amount;
    private String currency;
    private String status;


    public Payment(){

    }

    public Payment(String merchantId, String merchantCustomerId,String endToEndIdentifier,String amount,String currency,String status,String paymentId) {
        this.merchantId = merchantId;
        this.merchantCustomerId = merchantCustomerId;
        this.endToEndIdentifier = endToEndIdentifier;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paymentId=paymentId;

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
