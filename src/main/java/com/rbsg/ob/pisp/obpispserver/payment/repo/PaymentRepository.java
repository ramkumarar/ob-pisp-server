package com.rbsg.ob.pisp.obpispserver.payment.repo;

import com.rbsg.ob.pisp.obpispserver.payment.model.Payment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.paymentId=?1")
    Payment findByPaymentId(String paymentId);

}

