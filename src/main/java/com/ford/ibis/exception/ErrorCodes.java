package com.ford.ibis.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCodes {

    SYSTEM_EXCEPTION("TFL001", 500, "System exception occurred"),
    BAD_REQUEST("TFL002", 400, "Bad Request"),
    SQL_EXCEPTION("TFL003", 500, "SQL Exception"),
    REQUESTED_METHOD_NOT_ALLOWED("TFL004", 405,  "Requested http method not allowed");

    private final String errorCode;
    private final int httpStatus;
    private final String errorDescription;

}