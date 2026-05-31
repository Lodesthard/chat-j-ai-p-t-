package fr.ensim.interop.introrest.exception;

import org.springframework.http.HttpStatus;

public class TelegramApiException extends ApiException {
    public TelegramApiException(HttpStatus status, String message) {
        super(status, message);
    }
}
