package com.itachi.notification.repository.memory;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.itachi.notification.model.NotificationResponseStatus;
import com.itachi.notification.payload.response.NotificationResponseStore;
import com.itachi.notification.utility.NotificationUtility;

@Component
public class NotificationDataStore {
	private static ArrayList<NotificationResponseStore> responseDataStore;

	static {
		responseDataStore = new ArrayList<>();
	}

	public static ArrayList<NotificationResponseStore> fetchResponse() {
		return responseDataStore;
	}

	// store the response of the notification
	public void storeResponse(String[] emails, String clientID, String message, Date requestTime,
			NotificationResponseStatus status) {
		for (int i = 0; i < emails.length; i++) {
			String responseId = NotificationUtility.generateRandomId();
			NotificationResponseStore nr = new NotificationResponseStore(responseId, clientID, message, emails[i], status,
					requestTime);
			responseDataStore.add(nr);
		}
	}

}
