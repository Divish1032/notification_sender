package com.itachi.notification.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

import com.itachi.notification.exception.CustomErrorHandler;
import com.itachi.notification.model.NotificationDetail;
import com.itachi.notification.model.NotificationResponseStatus;
import com.itachi.notification.payload.response.NotificationResponseStore;
import com.itachi.notification.payload.response.SendNotificationResponseBody;
import com.itachi.notification.repository.memory.NotificationDataStore;
import com.itachi.notification.utility.NotificationUtility;

@Service
public class NotificationService {

	@Autowired
	MailService mailService;

	@Autowired
	PhoneService phoneService;

	@Autowired
	NotificationDataStore dataStore;

	public SendNotificationResponseBody sendNotification(NotificationDetail detail) {
		String message = detail.getMessage();
		String clientID = detail.getClientID();
		Date requestTime = detail.getRequestTime();

		// Filtering all the valid, invalid and duplicate email and phone
		ArrayList<String[]> filteredData = NotificationUtility.filterDestinationAddress(detail.getSentToDestination(),
				clientID, message);
		String[] validEmails = filteredData.get(0);
		String[] validPhones = filteredData.get(1);
		String[] invalidData = filteredData.get(2);
		String[] duplicateData = filteredData.get(3);

		// Store invalid input data in memory
		dataStore.storeResponse(invalidData, clientID, message, requestTime,
				NotificationResponseStatus.InvalidDestination);
		// Store duplicate input data in memory
		dataStore.storeResponse(duplicateData, clientID, message, requestTime, NotificationResponseStatus.Duplicate);

		// Send email to the given addresses
		this.sendEmailToDestination(validEmails, detail);
		// Send phone messages to given phone numbers
		this.sendPhoneMessageToDestination(validPhones, detail);

		return NotificationUtility.generateSendNotificationResponse(validEmails, validPhones, invalidData,
				duplicateData, detail);
	}

	public void sendEmailToDestination(String[] validEmails, NotificationDetail detail) {
		String message = detail.getMessage();
		String clientID = detail.getClientID();
		Date requestTime = detail.getRequestTime();

		// Max 3 attempts
		int count = 0;
		int maxTries = 3;
		while (count < 3) {
			try {
				// mailService.sendEmail(validEmails, detail.getMessageHeader(), message);
				dataStore.storeResponse(validEmails, clientID, message, requestTime,
						NotificationResponseStatus.Success);
				break;
			} catch (MailException e) {
				// handle exception
				if (++count == maxTries) {
					dataStore.storeResponse(validEmails, clientID, message, requestTime,
							NotificationResponseStatus.Failure);
				}
			}
		}
	}

	public void sendPhoneMessageToDestination(String[] validPhones, NotificationDetail detail) {
		String message = detail.getMessage();
		String clientID = detail.getClientID();
		Date requestTime = detail.getRequestTime();

		// Max 3 attempts
		int count = 0;
		int maxTries = 3;
		while (count < 3) {
			try {
				phoneService.sendPhoneMessage(validPhones, detail.getMessageHeader(), message);
				dataStore.storeResponse(validPhones, clientID, message, requestTime,
						NotificationResponseStatus.Success);
				break;
			} catch (Exception e) {
				// handle exception
				if (++count == maxTries) {
					dataStore.storeResponse(validPhones, clientID, message, requestTime,
							NotificationResponseStatus.Failure);
				}
			}
		}
	}

	public List<NotificationResponseStore> fetchQueryNotificationResult(String clientID, String status,
			String startTime, String endTime) throws CustomErrorHandler {
		// if one of the start time or end time given then return error as both has to
		// be given if query to be executed
		if ((startTime == null && endTime != null) || (startTime != null && endTime == null)) {
			throw new CustomErrorHandler("Please provide both start time and end time", 400);
		}
		// if client id not given then throw error.
		if (clientID == null) {
			throw new CustomErrorHandler("Client Id cannot be empty.", 400);
		}

		ArrayList<NotificationResponseStore> dataStore = NotificationDataStore.fetchResponse();

		Predicate<NotificationResponseStore> predicateClientId = new Predicate<NotificationResponseStore>() {
			@Override
			public boolean test(NotificationResponseStore s) {
				if (status != null && !(startTime != null && endTime != null)) {
					return s.getClientId().equalsIgnoreCase(clientID)
							&& s.getStatus().toString().equalsIgnoreCase(status);
				} else if (status == null && (startTime != null && endTime != null)) {
					Date startTimeDate = NotificationUtility.convertStringToDate(startTime);
					Date endTimeDate = NotificationUtility.convertStringToDate(endTime);
					return s.getClientId().equalsIgnoreCase(clientID)
							&& s.getStatus().toString().equalsIgnoreCase(status)
							&& s.getCreatedAt().after(startTimeDate) && s.getCreatedAt().before(endTimeDate);
				} else if (status == null && (startTime != null && endTime != null)) {
					Date startTimeDate = NotificationUtility.convertStringToDate(startTime);
					Date endTimeDate = NotificationUtility.convertStringToDate(endTime);
					return s.getClientId().equalsIgnoreCase(clientID) && s.getCreatedAt().after(startTimeDate)
							&& s.getCreatedAt().before(endTimeDate);
				} else {
					return s.getClientId().equalsIgnoreCase(clientID);
				}
			}
		};

		Stream<NotificationResponseStore> stream = dataStore.stream().filter(predicateClientId);
		return stream.collect(Collectors.toList());
	}

	public Map<Object, Long> generateReport() {
		ArrayList<NotificationResponseStore> dataStore = NotificationDataStore.fetchResponse();
		return dataStore.stream().collect(Collectors.groupingBy(s -> s.clientId, Collectors.counting()));
	}
}
