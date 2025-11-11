package com.mipa.common.dto.writerwsdto;

import com.mipa.common.Enum.WriterPageResponseType;
import com.mipa.common.Enum.WriterWSOp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerCommandSet {
	private WriterPageResponseType type;
	List<ServerCommand> commands;
}
