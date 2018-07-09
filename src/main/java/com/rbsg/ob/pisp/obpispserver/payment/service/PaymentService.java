package com.rbsg.ob.pisp.obpispserver.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.rbsg.ob.pisp.obpispserver.payment.model.*;
import com.rbsg.ob.pisp.obpispserver.payment.repo.MerchantRepository;
import com.rbsg.ob.pisp.obpispserver.payment.repo.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PaymentService {
    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    HttpRequestHelper httpRequestHelper;

    public PaymentResponseDTO setupPayment(PaymentRequestDTO paymentRequestDTO) {
         Optional<Merchant> merchant= merchantRepository.findById(paymentRequestDTO.getMerchantId());
         Payment payment = new Payment(paymentRequestDTO.getMerchantId(),paymentRequestDTO.getMerchantCustomerId(),paymentRequestDTO.getEndToEndIdentifier(),paymentRequestDTO.getAmount()
           ,"GBP","PAYMENT_INITIATION_REQUEST","");

        paymentRepository.save(payment);

        PaymentSetupPOSTRequest paymentSetupPOSTRequest= setupPaymentRequest(payment,merchant.get());

        String resourceUrl= new StringBuilder().append(httpRequestHelper.getBaseUrl()).append("/")
                .append("payments")
                .toString();

        HttpEntity<PaymentSetupPOSTRequest> entity = new HttpEntity<>(paymentSetupPOSTRequest, setupRequestHeaders());
        ResponseEntity<PaymentSetupPOSTResponse> response= restTemplate.exchange(resourceUrl, HttpMethod.POST, entity, PaymentSetupPOSTResponse.class);

        payment.setStatus(response.getBody().getData().getStatus().name());
        payment.setPaymentId(response.getBody().getData().getPaymentId());

        paymentRepository.save(payment);

        PaymentResponseDTO paymentResponseDTO= new PaymentResponseDTO();
        paymentResponseDTO.setPaymentId(response.getBody().getData().getPaymentId());
        paymentResponseDTO.setStatus(response.getBody().getData().getStatus().name());
        paymentResponseDTO.setEndToEndIdentifier(paymentRequestDTO.getEndToEndIdentifier());

        return paymentResponseDTO;
    }

    public PaymentResponseDTO setupPaymentSubmission(PaymentSubmissionRequestDTO paymentSubmissionRequestDTO) {

        Payment payment=paymentRepository.findByPaymentId(paymentSubmissionRequestDTO.getPaymentId());
        Optional<Merchant> merchant= merchantRepository.findById(payment.getMerchantId());
        String authToken=fetchAuthToken(paymentSubmissionRequestDTO.getOauthCode(),httpRequestHelper.getAuthUrl());



        PaymentSubmissionPOSTRequest paymentSubmissionPOSTRequest= paymentSubmissionPOSTRequest(payment,merchant.get());

        String resourceUrl= new StringBuilder().append(httpRequestHelper.getBaseUrl()).append("/")
                .append("/payment-submissions")
                .toString();

        HttpHeaders headers=setupRequestHeaders();
        headers.remove("Authorization");
        headers.add("Authorization","Bearer " + authToken);

        HttpEntity<PaymentSubmissionPOSTRequest> entity = new HttpEntity<PaymentSubmissionPOSTRequest>(paymentSubmissionPOSTRequest, headers);
        ResponseEntity<PaymentSubmitPOST201Response> response= restTemplate.exchange(resourceUrl, HttpMethod.POST, entity, PaymentSubmitPOST201Response.class);

        payment.setStatus(response.getBody().getData().getStatus().name());


        paymentRepository.save(payment);

        PaymentResponseDTO paymentResponseDTO=new PaymentResponseDTO();
        paymentResponseDTO.setEndToEndIdentifier(payment.getEndToEndIdentifier());
        paymentResponseDTO.setStatus(payment.getStatus());
        paymentResponseDTO.setPaymentId(payment.getPaymentId());
        paymentResponseDTO.setAmount(payment.getAmount());
        paymentResponseDTO.setCurrency(payment.getCurrency());

        return paymentResponseDTO;
    }

    private String fetchAuthToken(String code,String url) {

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", "pisp");
        map.add("redirect_uri", "http://example.com");
        map.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, fetchTokenExchangeHeaders());
        ResponseEntity<JsonNode> response = restTemplate.postForEntity( url, request , JsonNode.class );

        String accessToken=response.getBody().get("access_token").asText();

       return accessToken;

    }

    private HttpHeaders fetchTokenExchangeHeaders(){
        String encodedValue = Base64.getEncoder().encodeToString(("pisp:secret").getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization","Basic " +encodedValue);
        return headers;
    }

    private PaymentSetupPOSTRequest setupPaymentRequest(Payment payment,Merchant merchant){


    PaymentSetup paymentSetup= new PaymentSetup();
    PaymentSetupInitiation paymentSetupInitiation = new PaymentSetupInitiation();

    CreditorAccount creditorAccount= new CreditorAccount();
    creditorAccount.setSchemeName(CreditorAccount.SchemeNameEnum.IBAN);
    creditorAccount.setIdentification(merchant.getAccountnumber());
    creditorAccount.setName(merchant.getName());
    creditorAccount.setSecondaryIdentification("12345");

    CreditorAgent creditorAgent= new CreditorAgent();
    creditorAgent.setIdentification(merchant.getSortcode());
    creditorAgent.setSchemeName(CreditorAgent.SchemeNameEnum.SORTCODEACCOUNTNUMBER);

    DebtorAccount debtorAccount= new DebtorAccount();
    debtorAccount.setSchemeName(DebtorAccount.SchemeNameEnum.IBAN);
    debtorAccount.setIdentification("1705541617319936");
    debtorAccount.setName("Amazon Inc");
    debtorAccount.setSecondaryIdentification("702323091308544");

    DebtorAgent debtorAgent=new DebtorAgent();
    debtorAgent.setIdentification("7840651708203008");
    debtorAgent.setSchemeName(DebtorAgent.SchemeNameEnum.BICFI);

    RemittanceInformation remittanceInformation= new RemittanceInformation();
    remittanceInformation.setReference("amazon shopping payment reference");
    remittanceInformation.setUnstructured("payment for my shopping");

    PaymentSetupInitiationInstructedAmount paymentSetupInitiationInstructedAmount = new PaymentSetupInitiationInstructedAmount();
    paymentSetupInitiationInstructedAmount.setAmount(payment.getAmount());
    paymentSetupInitiationInstructedAmount.setCurrency("GBP");

    paymentSetupInitiation.setCreditorAccount(creditorAccount);
    paymentSetupInitiation.setCreditorAgent(creditorAgent);
    paymentSetupInitiation.setDebtorAccount(debtorAccount);
    paymentSetupInitiation.setDebtorAgent(debtorAgent);
    paymentSetupInitiation.setRemittanceInformation(remittanceInformation);
    paymentSetupInitiation.setEndToEndIdentification(payment.getEndToEndIdentifier());
    paymentSetupInitiation.setInstructionIdentification(UUID.randomUUID().toString().substring(0,22));
    paymentSetupInitiation.setInstructedAmount(paymentSetupInitiationInstructedAmount);


    paymentSetup.setInitiation(paymentSetupInitiation);

    Risk risk = new Risk();
    risk.setPaymentContextCode(Risk.PaymentContextCodeEnum.BILLPAYMENT);
    risk.setMerchantCategoryCode("ONRT");
    risk.setMerchantCustomerIdentification(payment.getMerchantCustomerId());
    RiskDeliveryAddress riskDeliveryAddress = new RiskDeliveryAddress();
    List<String> addressline = new ArrayList<>();
    addressline.add("elrunegoabifezukecoizbuosiuludawdobewokasuzegeihz");

    List<String> subdivision = new ArrayList<>();
    subdivision.add("merusjowisifeomhae");

    riskDeliveryAddress.setAddressLine(addressline);
    riskDeliveryAddress.setBuildingNumber("78986");
    riskDeliveryAddress.setCountry("UK");

    riskDeliveryAddress.setPostCode("C4G 5S9");
    riskDeliveryAddress.setStreetName("Hetva Highway");
    riskDeliveryAddress.setTownName("Troy Walton");
    riskDeliveryAddress.setCountrySubDivision(subdivision);
    risk.setDeliveryAddress(riskDeliveryAddress);


    PaymentSetupPOSTRequest paymentSetupPOSTRequest = new PaymentSetupPOSTRequest();
    paymentSetupPOSTRequest.setData(paymentSetup);
    paymentSetupPOSTRequest.setRisk(risk);


   return  paymentSetupPOSTRequest;

}


    private PaymentSubmissionPOSTRequest paymentSubmissionPOSTRequest(Payment payment,Merchant merchant){


        PaymentSubmission paymentSubmission= new PaymentSubmission();
        PaymentSetupResponseInitiation paymentSetupResponseInitiation = new PaymentSetupResponseInitiation();

        CreditorAccount creditorAccount= new CreditorAccount();
        creditorAccount.setSchemeName(CreditorAccount.SchemeNameEnum.IBAN);
        creditorAccount.setIdentification(merchant.getAccountnumber());
        creditorAccount.setName(merchant.getName());
        creditorAccount.setSecondaryIdentification("12345");

        CreditorAgent creditorAgent= new CreditorAgent();
        creditorAgent.setIdentification(merchant.getSortcode());
        creditorAgent.setSchemeName(CreditorAgent.SchemeNameEnum.SORTCODEACCOUNTNUMBER);

        DebtorAccount debtorAccount= new DebtorAccount();
        debtorAccount.setSchemeName(DebtorAccount.SchemeNameEnum.IBAN);
        debtorAccount.setIdentification("1705541617319936");
        debtorAccount.setName("Amazon Inc");
        debtorAccount.setSecondaryIdentification("702323091308544");

        DebtorAgent debtorAgent=new DebtorAgent();
        debtorAgent.setIdentification("7840651708203008");
        debtorAgent.setSchemeName(DebtorAgent.SchemeNameEnum.BICFI);

        RemittanceInformation remittanceInformation= new RemittanceInformation();
        remittanceInformation.setReference("amazon shopping payment reference");
        remittanceInformation.setUnstructured("payment for my shopping");

        PaymentSetupInitiationInstructedAmount paymentSetupInitiationInstructedAmount = new PaymentSetupInitiationInstructedAmount();
        paymentSetupInitiationInstructedAmount.setAmount(payment.getAmount());
        paymentSetupInitiationInstructedAmount.setCurrency("GBP");

        paymentSetupResponseInitiation.setCreditorAccount(creditorAccount);
        paymentSetupResponseInitiation.setCreditorAgent(creditorAgent);
        paymentSetupResponseInitiation.setDebtorAccount(debtorAccount);
        paymentSetupResponseInitiation.setDebtorAgent(debtorAgent);
        paymentSetupResponseInitiation.setRemittanceInformation(remittanceInformation);
        paymentSetupResponseInitiation.setEndToEndIdentification(payment.getEndToEndIdentifier());
        paymentSetupResponseInitiation.setInstructionIdentification(UUID.randomUUID().toString().substring(0,22));
        paymentSetupResponseInitiation.setInstructedAmount(paymentSetupInitiationInstructedAmount);


        paymentSubmission.setInitiation(paymentSetupResponseInitiation);
        paymentSubmission.setPaymentId(payment.getPaymentId());

        Risk risk = new Risk();
        risk.setPaymentContextCode(Risk.PaymentContextCodeEnum.BILLPAYMENT);
        risk.setMerchantCategoryCode("ONRT");
        risk.setMerchantCustomerIdentification(payment.getMerchantCustomerId());
        RiskDeliveryAddress riskDeliveryAddress = new RiskDeliveryAddress();
        List<String> addressline = new ArrayList<>();
        addressline.add("elrunegoabifezukecoizbuosiuludawdobewokasuzegeihz");

        List<String> subdivision = new ArrayList<>();
        subdivision.add("merusjowisifeomhae");

        riskDeliveryAddress.setAddressLine(addressline);
        riskDeliveryAddress.setBuildingNumber("78986");
        riskDeliveryAddress.setCountry("UK");

        riskDeliveryAddress.setPostCode("C4G 5S9");
        riskDeliveryAddress.setStreetName("Hetva Highway");
        riskDeliveryAddress.setTownName("Troy Walton");
        riskDeliveryAddress.setCountrySubDivision(subdivision);
        risk.setDeliveryAddress(riskDeliveryAddress);


        PaymentSubmissionPOSTRequest paymentSubmissionPOSTRequest = new PaymentSubmissionPOSTRequest();
        paymentSubmissionPOSTRequest.setData(paymentSubmission);
        paymentSubmissionPOSTRequest.setRisk(risk);


        return  paymentSubmissionPOSTRequest;

    }

private HttpHeaders setupRequestHeaders(){

    HttpHeaders headers = new HttpHeaders();


    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    headers.set("Authorization","");
    headers.set("x-idempotency-key","12345678");
    headers.set("x-fapi-financial-id","1");

    return headers;
}


}
