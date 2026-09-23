package com.hospital.exception;

public class DuplicateUsernameException extends InvalidDataException {
    public DuplicateUsernameException(String message) { super(message); }
}
