package com.mipa.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimerTask {

	@Autowired
	ContentPageCacheSaveToDBService saveToDBService;

	@Scheduled(cron = "0/20 * * * * ?")
	public void cronTask() {
		log.debug("定时落库");
		saveToDBService.saveToDB();
	}
}
