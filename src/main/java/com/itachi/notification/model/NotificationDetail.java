package com.itachi.notification.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDetail {
	private String messageHeader;
	private String message;
	private String [] sentToDestination;
	private String clientID;
	private Date requestTime;
}
