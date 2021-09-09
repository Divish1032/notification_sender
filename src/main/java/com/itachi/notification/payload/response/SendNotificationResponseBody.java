package com.itachi.notification.payload.response;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationResponseBody {
	private String clientId;
	private String message;
	private String [] requestSuccess;
	private String [] requestInvalid;
	private String [] requestDuplicate;
	private Date createdAt;	
}
