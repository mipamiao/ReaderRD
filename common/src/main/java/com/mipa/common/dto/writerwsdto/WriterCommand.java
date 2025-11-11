package com.mipa.common.dto.writerwsdto;

import com.mipa.common.Enum.WriterWSOp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriterCommand {
	private WriterWSOp type;
	Integer startPos;
	Integer otherPos;
	Integer num;
	String data;
}
