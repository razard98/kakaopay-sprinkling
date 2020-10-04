package kr.per.james.kakaopay.sprinkling.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
class ErrorMessage {

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("status")
    private int status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("message")
    private String message;

    @JsonProperty("path")
    private String path;

    ErrorMessage(Exception exception, String path, HttpStatus httpStatus) {
        this.message = exception.getMessage();
        this.error = httpStatus.getReasonPhrase();
        this.path = path;
        this.status = httpStatus.value();
        this.timestamp = LocalDateTime.now();
    }
}
