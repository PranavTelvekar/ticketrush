package com.ticketrush.payment;

import com.ticketrush.booking.Booking;
import com.ticketrush.booking.BookingStatus;
import com.ticketrush.common.entity.BaseEntity;
import com.ticketrush.common.exception.BusinessRuleException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * The (mock) payment for one booking. The idempotency key makes sure a retried
 * "Pay" request can never create a second payment.
 */
@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "transaction_ref", length = 100)
    private String transactionRef;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** Starts a payment for a PENDING booking. The amount always comes from the booking, never from the client. */
    public Payment(Booking booking, String idempotencyKey) {
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BusinessRuleException("Only PENDING bookings can be paid (current status: " + booking.getStatus() + ")");
        }
        this.booking = booking;
        this.amount = booking.getTotalAmount();
        this.idempotencyKey = idempotencyKey;
        this.status = PaymentStatus.PROCESSING;
    }

    /** PROCESSING -> SUCCESS (the payment gateway said yes). */
    public void markSuccess(String transactionRef) {
        if (status != PaymentStatus.PROCESSING) {
            throw new BusinessRuleException("Payment " + getId() + " is already finished (status: " + status + ")");
        }
        this.transactionRef = transactionRef;
        status = PaymentStatus.SUCCESS;
    }

    /** PROCESSING -> FAILED (the payment gateway said no). */
    public void markFailed() {
        if (status != PaymentStatus.PROCESSING) {
            throw new BusinessRuleException("Payment " + getId() + " is already finished (status: " + status + ")");
        }
        status = PaymentStatus.FAILED;
    }
}
