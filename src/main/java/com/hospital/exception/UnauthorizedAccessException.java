package com.hospital.exception;

public class UnauthorizedAccessException extends InvalidDataException {
    public UnauthorizedAccessException(String message) { super(message); }
}
