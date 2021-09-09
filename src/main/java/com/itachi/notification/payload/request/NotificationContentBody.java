package com.itachi.notification.payload.request;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationContentBody {
	@NotBlank
	private String messageHeader;
	@NotBlank
	private String message;
	private String [] sentToDestination;
}
