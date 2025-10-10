package com.mipa.websocket.handler;

import jakarta.websocket.Session;

import java.io.IOException;

public class WebSocketExceptionHandler {

	public static void handle(Session session, Throwable throwable) {
		// 打印日志
		throwable.printStackTrace();
		// 通知客户端
		try {
			session.getBasicRemote().sendText("错误：" + throwable.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
