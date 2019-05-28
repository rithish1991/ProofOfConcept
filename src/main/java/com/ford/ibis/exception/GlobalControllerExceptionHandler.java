package com.ford.ibis.exception;


import static com.ford.ibis.exception.ErrorCodes.BAD_REQUEST;
import static com.ford.ibis.exception.ErrorCodes.REQUESTED_METHOD_NOT_ALLOWED;
import static com.ford.ibis.exception.ErrorCodes.SYSTEM_EXCEPTION;
import static com.ford.ibis.exception.ErrorCodes.SQL_EXCEPTION;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ford.ibis.model.util.ErrorResponse;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.marker.Markers;

@Slf4j
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ServletRequestBindingException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleServerBindingException(final ServletRequestBindingException srbe) {
        log.error("In ServletRequestBindingException exception handler");
        return ResponseEntity.status(BAD_REQUEST.getHttpStatus())
                             .body(buildErrorResponse(BAD_REQUEST, srbe.getMessage()));
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleHttpMethodNotSupportedException(
                                         final HttpRequestMethodNotSupportedException hrmnse) {
        log.error("In HttpRequestMethodNotSupported exception handler:{}", hrmnse.getMessage());
        return ResponseEntity.status(REQUESTED_METHOD_NOT_ALLOWED.getHttpStatus())
                .body(buildErrorResponse(REQUESTED_METHOD_NOT_ALLOWED, REQUESTED_METHOD_NOT_ALLOWED.getErrorDescription()));
    }
    
    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleSQLException(
                                         final SQLException sqle) {
        log.error("In SQLExcewption exception handler:{}", sqle.getMessage());
        return ResponseEntity.status(SQL_EXCEPTION.getHttpStatus())
                .body(buildErrorResponse(SQL_EXCEPTION, SQL_EXCEPTION.getErrorDescription()));
    }

    private ErrorResponse buildErrorResponse(final ErrorCodes errorCodes, final String errorDescription) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                                                   .errorCode(errorCodes.getErrorCode())
                                                   .errorMessage(errorCodes.name())
                                                   .errorDescription(errorDescription)
                                                   .build();
        log.error(Markers.append("exception", errorResponse), null);
        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleOtherExceptions(final Exception e) {
        log.error("In handle All Exception:{}", e.getMessage(), e);
        return ResponseEntity.status(SYSTEM_EXCEPTION.getHttpStatus())
                             .body(ErrorResponse.builder()
                                                .errorCode(SYSTEM_EXCEPTION.getErrorCode())
                                                .errorMessage(SYSTEM_EXCEPTION.name())
                                                .errorDescription(SYSTEM_EXCEPTION.getErrorDescription())
                                                .build());
    }

}
