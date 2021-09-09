package com.itachi.notification.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.itachi.notification.model.NotificationDetail;
import com.itachi.notification.payload.response.NotificationResponseStore;
import com.itachi.notification.payload.response.SendNotificationResponseBody;
import com.itachi.notification.repository.memory.NotificationDataStore;

public class NotificationUtility {

	// check if given destination is present in the filtered data store
	public static boolean checkDuplicateNotification(ArrayList<NotificationResponseStore> dataStore, String destination) {
		for (int i = 0; i < dataStore.size(); i++) {
			if (dataStore.get(i).getSentTo().equalsIgnoreCase(destination)) {
				return true;
			}
		}
		return false;
	}

	// generate random id
	public static String generateRandomId() {
		return UUID.randomUUID().toString();
	}

	// check if email is valid
	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}

	/*
	 * The first digit should contain number between 7 to 9. The rest 9 digit can
	 * contain any number between 0 to 9. The mobile number can have 11 digits also
	 * by including 0 at the starting. The mobile number can be of 12 digits also by
	 * including 91 at the starting
	 */
	public static boolean isValidPhoneNumber(String s) {
		Pattern p = Pattern.compile("(0|91)?[7-9][0-9]{9}");
		Matcher m = p.matcher(s);
		return (m.find() && m.group().equals(s));
	}

	// Filter out every destination address given i.e. in email and phone number based on valid, invalid and duplicate. 
	public static ArrayList<String[]> filterDestinationAddress(String[] destination, String clientID, String message) {
		ArrayList<String[]> filteredDataAggregate = new ArrayList<String[]>();
		ArrayList<String> validEmails = new ArrayList<>();
		ArrayList<String> validPhones = new ArrayList<>();
		ArrayList<String> invalidData = new ArrayList<>();
		ArrayList<String> duplicateData = new ArrayList<>();

		// fetch data store and filter it out with given client id, message and time
		// interval of recent 5 mins
		ArrayList<NotificationResponseStore> dataStore = NotificationDataStore.fetchResponse();
		

		Predicate<NotificationResponseStore> predicate = new Predicate<NotificationResponseStore>() {
            @Override
            public boolean test(NotificationResponseStore s)
            {
                return s.getCreatedAt().after(new Date(System.currentTimeMillis() - 300 * 1000))
        				&& s.getMessage().equalsIgnoreCase(message) && s.getClientId().equalsIgnoreCase(clientID);
            }
        };
        
        dataStore = (ArrayList<NotificationResponseStore>) dataStore.stream().filter(predicate).collect(Collectors.toList());
        
		for (int i = 0; i < destination.length; i++) {
			String path = destination[i];
			if (isValidPhoneNumber(path)) {
				if (checkDuplicateNotification(dataStore, path)) {
					duplicateData.add(path);
				} else {
					validPhones.add(path);
				}

			} else if (isValidEmailAddress(path)) {
				if (checkDuplicateNotification(dataStore, path)) {
					duplicateData.add(path);
				} else {
					validEmails.add(path);
				}

			} else {
				invalidData.add(path);
			}
		}
		filteredDataAggregate.add(convertListToArray(validEmails));
		filteredDataAggregate.add(convertListToArray(validPhones));
		filteredDataAggregate.add(convertListToArray(invalidData));
		filteredDataAggregate.add(convertListToArray(duplicateData));
		return filteredDataAggregate;
	}

	// generate the response for the send notification api
	public static SendNotificationResponseBody generateSendNotificationResponse(String[] validEmails,
			String[] validPhones, String[] invalidData, String[] duplicateData, NotificationDetail detail) {
		String[] success = Stream.concat(Arrays.stream(validEmails), Arrays.stream(validPhones)).toArray(String[]::new);
		SendNotificationResponseBody res = new SendNotificationResponseBody(detail.getClientID(), detail.getMessage(),
				success, invalidData, duplicateData, new Date());
		return res;
	}
	
	// convert a list to array
	public static String[] convertListToArray(ArrayList<String> data) {
		String[] res = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			res[i] = data.get(i);
		}
		return res;
	}
	
	// convert string date to Date format
	public static Date convertStringToDate(String date) {
		SimpleDateFormat formatter=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");  
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			return new Date();
		}  
	}
}
