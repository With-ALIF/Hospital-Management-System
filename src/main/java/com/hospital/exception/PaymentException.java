package com.hospital.exception;

public class PaymentException extends InvalidDataException {
    public PaymentException(String message) {
        super(message);
    }
}
