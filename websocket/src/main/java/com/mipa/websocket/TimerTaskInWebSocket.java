package com.mipa.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TimerTaskInWebSocket {


	WriterWebSocket writerWebSocket;

	@Autowired
	public TimerTaskInWebSocket(WriterWebSocket writerWebSocket){
		this.writerWebSocket = writerWebSocket;
	}

	@Scheduled(cron = "0/20 * * * * ?")
	public void cronTask() {
		log.debug("自动保活");
		writerWebSocket.keepAlive();
	}
}
