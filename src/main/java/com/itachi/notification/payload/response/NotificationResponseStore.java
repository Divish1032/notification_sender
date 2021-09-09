package com.itachi.notification.payload.response;

import java.util.Date;

import com.itachi.notification.model.NotificationResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NotificationResponseStore {
	public String responseID;
	public String clientId;
	public String message;
	public String sentTo;
	public NotificationResponseStatus status;
	public Date createdAt;
	
	@Override
	public String toString() {
		return "ResponseId: " + this.responseID + ", client Id: " + this.clientId + ", message: " + this.message + ", status: " + this.status;
	}
	
	
}
