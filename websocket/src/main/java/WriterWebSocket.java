import com.mipa.common.dto.writerwsdto.WriterCommandSet;
import com.mipa.common.dto.writerwsdto.WriterWsRequestDTO;
import com.mipa.common.utils.JsonUtils;
import com.mipa.common.utils.StringUtils;
import com.mipa.service.api.IContentPageService;
import handler.WebSocketExceptionHandler;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/writer/{userId}/{chapterId}")
public class WriterWebSocket {

	static IContentPageService pageService;

	@Autowired
	public void setPageService(IContentPageService pageService) {
		WriterWebSocket.pageService = pageService;
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

		var session = userIdchapterId2SessionMap.get(userId + "_" + chapterId);
		try {
			var commands = JsonUtils.parseJson(message, WriterCommandSet.class);
			var serverCommands = pageService.scheduleOp(commands, userId, chapterId);
			session.getBasicRemote().sendText(JsonUtils.toJson(serverCommands));
		} catch (Exception e) {
			WebSocketExceptionHandler.handle(session, e);
		}

	}

	@OnClose
	public void onClose(@PathParam("userId") String userId) {
		System.out.println("连接关闭");
		userIdchapterId2SessionMap.remove(userId);
	}

	@OnError
	public void onError(Session session, Throwable error) {
		WebSocketExceptionHandler.handle(session, error);
	}


}
