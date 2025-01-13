package com.hana4.ggumtle.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String customMessage;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = null;
    }

    public CustomException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    @Override
    public String getMessage() {
        return customMessage != null ? customMessage : errorCode.getMessage();
    }
}
