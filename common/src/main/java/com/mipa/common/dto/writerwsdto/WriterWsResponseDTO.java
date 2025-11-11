package com.mipa.common.dto.writerwsdto;

import com.mipa.common.Enum.WriterWSIndexOp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriterWsResponseDTO {
	private WriterWSIndexOp op;
	private Integer pos;
	private Integer len;
	private List<String> indexs;
}
