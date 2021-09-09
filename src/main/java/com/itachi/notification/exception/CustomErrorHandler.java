package com.itachi.notification.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@ResponseStatus(code=HttpStatus.BAD_REQUEST)
public class CustomErrorHandler extends Exception {
	private static final long serialVersionUID = 1L;
	private final String message;
	private final int status;

}
