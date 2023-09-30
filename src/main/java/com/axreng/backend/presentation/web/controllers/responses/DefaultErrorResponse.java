package com.axreng.backend.presentation.web.controllers.responses;

public class DefaultErrorResponse {

    private final String message;
    private Integer status;


    public Integer getStatus() {
        return status;
    }

    public DefaultErrorResponse(String message, Exception error) {
        this.message = message;
        if (error instanceof IllegalArgumentException) {
            this.status = 400;
        } else {
            this.status= 500;
        }

    }

    public String getMessage() {
        return message;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
