package com.cs.market.global.exception;

public class PaymentProviderException extends RuntimeException{

    private final int httpStatus;
    private final String responseBody;

    public PaymentProviderException(String message) {
        super(message);
        this.httpStatus = -1;
        this.responseBody = null;
    }

    public PaymentProviderException(String message, int httpStatus, String responseBody) {
        super(message);
        this.httpStatus = httpStatus;
        this.responseBody = responseBody;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

}
