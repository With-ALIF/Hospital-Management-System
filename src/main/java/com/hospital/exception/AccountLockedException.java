package com.hospital.exception;

public class AccountLockedException extends InvalidDataException {
    public AccountLockedException(String message) { super(message); }
}
