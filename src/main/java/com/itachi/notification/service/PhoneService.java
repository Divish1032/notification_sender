package com.itachi.notification.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

@Service
public class PhoneService {


	@Value("${twilio.account_sid}")
	private String ACCOUNT_SID;
	@Value("${twilio.auth_token}")
	private String AUTH_TOKEN;
	@Value("${twilio.account_number}")
	private String TRILLO_ACCOUNT_NUMBER;
	
	// trail account from twilio, so limited the use of the phone number message
	public void sendPhoneMessage(String[] validPhones, String messageHeader, String data) {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		System.out.println(validPhones.length);
		for (int i = 0; i < validPhones.length; i++) {
			Message.creator(new com.twilio.type.PhoneNumber("+91" + validPhones[i]),
					new com.twilio.type.PhoneNumber(TRILLO_ACCOUNT_NUMBER), data).create();
			// due to the error thrown by the usage of free version we can only send otp to the verified number on twilio account.
			break;
		}
	}

}
