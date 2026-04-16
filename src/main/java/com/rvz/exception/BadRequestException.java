package com.rvz.exception;


public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) { super(msg); }
}