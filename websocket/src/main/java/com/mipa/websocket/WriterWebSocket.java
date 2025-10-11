package com.mipa.websocket;

import com.mipa.common.dto.writerwsdto.WriterCommandSet;
import com.mipa.common.utils.JsonUtils;
import com.mipa.service.api.IContentPageService;
import com.mipa.service.api.ITopOfContentPageService;
import com.mipa.websocket.handler.WebSocketExceptionHandler;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ServerEndpoint("/ws/writer/{userId}/{chapterId}")
public class WriterWebSocket {

	private static final ExecutorService executor = Executors.newFixedThreadPool(10);

	static ITopOfContentPageService topOfPageService;

	@Autowired
	public void setPageService(ITopOfContentPageService topOfPageService) {
		WriterWebSocket.topOfPageService = topOfPageService;
	}


	private static  final Map<String, String> id2UserIdMap = new ConcurrentHashMap<>();
	private static final Map<String, Session> userIdchapterId2SessionMap = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("userId") String userId, @PathParam("chapterId") String chapterId) {

		System.out.println("连接成功：" + session.getId());
		userIdchapterId2SessionMap.put(userId + "_" + chapterId, session);
	}

	@OnMessage
	public void onMessage(String message, @PathParam("userId") String userId, @PathParam("chapterId") String chapterId) {

		executor.submit(() -> {
			var session = userIdchapterId2SessionMap.get(userId + "_" + chapterId);
			try {
				var commands = JsonUtils.parseJson(message, WriterCommandSet.class);
				var serverCommands = topOfPageService.scheduleOp(commands, userId, chapterId);
				session.getBasicRemote().sendText(JsonUtils.toJson(serverCommands));
			} catch (Exception e) {
				WebSocketExceptionHandler.handle(session, e);
			}
		});

	}

	@OnClose
	public void onClose(@PathParam("userId") String userId, @PathParam("chapterId") String chapterId) {
		System.out.println("连接关闭");
		userIdchapterId2SessionMap.remove(userId + "_" + chapterId);
	}

	@OnError
	public void onError(Session session, Throwable error) {
		WebSocketExceptionHandler.handle(session, error);
	}


}
