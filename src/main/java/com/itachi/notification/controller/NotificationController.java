package com.itachi.notification.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.itachi.notification.exception.CustomErrorHandler;
import com.itachi.notification.model.NotificationDetail;
import com.itachi.notification.payload.request.NotificationContentBody;
import com.itachi.notification.payload.request.NotificationRequestBody;
import com.itachi.notification.payload.response.NotificationResponseStore;
import com.itachi.notification.payload.response.SendNotificationResponseBody;
import com.itachi.notification.service.NotificationService;

@CrossOrigin(origins = "*")
@RestController
public class NotificationController {

	@Autowired
	NotificationService notificationService;

	@GetMapping("/sendNotification")
	@ResponseBody
	public ResponseEntity<?> create(@Valid @RequestBody NotificationRequestBody incommingData) throws ParseException {
		NotificationContentBody[] content = incommingData.getContent();
		Date date = new Date();
		ArrayList<SendNotificationResponseBody> response = new ArrayList<>();
		for (int i = 0; i < content.length; i++) {
			NotificationContentBody contentBody = content[i];
			NotificationDetail detail = new NotificationDetail(contentBody.getMessageHeader(), contentBody.getMessage(),
					contentBody.getSentToDestination(), incommingData.getClientId(), date);
			SendNotificationResponseBody notificationResponse = notificationService.sendNotification(detail);
			response.add(notificationResponse);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "fetchNotificationData/{clientID}", method = RequestMethod.GET)
	public ResponseEntity<?> fetchNotification(@PathVariable("clientID") String clientID,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime) throws CustomErrorHandler {
		List<NotificationResponseStore> result = notificationService.fetchQueryNotificationResult(clientID, status, startTime, endTime);
		return new ResponseEntity<List<NotificationResponseStore>>(result, HttpStatus.OK);
	}
	
	@RequestMapping(value = "generateReport", method = RequestMethod.GET)
	public Map<Object, Long> generateReport() throws CustomErrorHandler {
		return notificationService.generateReport();
	}

}
