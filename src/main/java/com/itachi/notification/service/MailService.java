package com.itachi.notification.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class MailService {

	private JavaMailSender javaMailSender;

	@Autowired
	public MailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendEmail(String[] emails, String header, String message) throws MailException {

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setBcc(emails);
		mail.setSubject(header);
		mail.setText(message);

		javaMailSender.send(mail);
	}

}