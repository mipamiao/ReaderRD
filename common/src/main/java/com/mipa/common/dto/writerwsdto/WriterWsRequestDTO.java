package com.mipa.common.dto.writerwsdto;

import com.mipa.common.Enum.WriterWSOp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WriterWsRequestDTO {
	WriterWSOp op;
	Integer pageId;
	Integer pos;
	Long length;
	String data;
}
