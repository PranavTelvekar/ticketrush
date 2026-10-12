package com.ticketrush.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /** Was this "Pay" request seen before? (same Idempotency-Key header) */
    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    Optional<Payment> findByBookingId(Long bookingId);
}
