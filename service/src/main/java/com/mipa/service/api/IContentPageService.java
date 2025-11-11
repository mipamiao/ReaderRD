package com.mipa.service.api;

import com.mipa.common.dto.writerwsdto.ServerCommandSet;
import com.mipa.common.dto.writerwsdto.WriterCommandSet;

public interface IContentPageService {
	ServerCommandSet scheduleOp(WriterCommandSet commands, String userId, String chapterId);
}
