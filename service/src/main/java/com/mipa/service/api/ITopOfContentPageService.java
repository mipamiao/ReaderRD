package com.mipa.service.api;

import com.mipa.common.dto.writerwsdto.ServerCommandSet;
import com.mipa.common.dto.writerwsdto.WriterCommand;
import com.mipa.common.dto.writerwsdto.WriterCommandSet;
import com.mipa.model.Chapter;
import org.springframework.transaction.annotation.Transactional;

public interface ITopOfContentPageService {
	@Transactional
	ServerCommandSet scheduleOp(WriterCommandSet commands, String userId, String chapterId, Boolean tryCache);

	void getOp(String userId, Chapter chapter, WriterCommand command, ServerCommandSet serverCommands);

	void getInfoOp(String userId, Chapter chapter, WriterCommand command, ServerCommandSet serverCommands);
}
