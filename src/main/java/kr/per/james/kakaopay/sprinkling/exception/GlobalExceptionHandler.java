package kr.per.james.kakaopay.sprinkling.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorMessage> error(final Exception exception, final HttpStatus httpStatus,
                                               HttpServletRequest request) {
        return new ResponseEntity<>(new ErrorMessage(exception, request.getRequestURI(), httpStatus), httpStatus);
    }

    @ExceptionHandler(InquiryForbiddenException.class)
    public ResponseEntity<ErrorMessage> handleNotFoundSprinklingException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ExpiredInquiryException.class)
    public ResponseEntity<ErrorMessage> handleExpiredInquiryException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(SelfAssignedException.class)
    public ResponseEntity<ErrorMessage> handleSelfAssignedException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(NotBelongRoomException.class)
    public ResponseEntity<ErrorMessage> handleNotBelongRoomException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ExpiredSprinklingException.class)
    public ResponseEntity<ErrorMessage> handleExpiredSprinklingException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(AlreadyAssignedException.class)
    public ResponseEntity<ErrorMessage> handleAlreadyAssignedException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(SprinklingCompletedException.class)
    public ResponseEntity<ErrorMessage> handleSprinklingCompletedException(HttpServletRequest request, final RuntimeException e) {
        return error(e, HttpStatus.FORBIDDEN, request);
    }
}
