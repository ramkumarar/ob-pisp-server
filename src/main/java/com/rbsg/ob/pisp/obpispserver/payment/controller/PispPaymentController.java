package com.rbsg.ob.pisp.obpispserver.payment.controller;

import com.rbsg.ob.pisp.obpispserver.payment.model.PaymentRequestDTO;
import com.rbsg.ob.pisp.obpispserver.payment.model.PaymentResponseDTO;
import com.rbsg.ob.pisp.obpispserver.payment.model.PaymentSetupPOSTResponse;
import com.rbsg.ob.pisp.obpispserver.payment.model.PaymentSubmissionRequestDTO;
import com.rbsg.ob.pisp.obpispserver.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PispPaymentController {

    @Autowired
    private PaymentService paymentService;

    @RequestMapping(value = "/payments",
            produces = { "application/json; charset=utf-8" },
            consumes = { "application/json; charset=utf-8" },
            method = RequestMethod.POST)

    public PaymentResponseDTO greeeting(@Valid @RequestBody PaymentRequestDTO paymentRequestDTO){
        return paymentService.setupPayment(paymentRequestDTO);
    }

    @RequestMapping(value = "/payments-submission",
            produces = { "application/json; charset=utf-8" },
            consumes = { "application/json; charset=utf-8" },
            method = RequestMethod.POST)

    public PaymentResponseDTO greeeting(@Valid @RequestBody PaymentSubmissionRequestDTO paymentSubmissionRequestDTO){
        return paymentService.setupPaymentSubmission(paymentSubmissionRequestDTO);
    }

}

