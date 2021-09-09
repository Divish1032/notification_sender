package com.itachi.notification.payload.request;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestBody {
	@Valid
	private NotificationContentBody [] content;
	
	@NotBlank
	private String clientId;
}
