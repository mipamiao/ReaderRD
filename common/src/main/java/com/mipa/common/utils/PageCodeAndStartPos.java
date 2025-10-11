package com.mipa.common.utils;

import com.mipa.common.Enum.PagePosKind;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageCodeAndStartPos {
	private Integer pageCode;
	private Integer pageWordCount;
	private Integer startPos;
	private PagePosKind kind;

	public PagePosKind getKind() {
		if(startPos == 0)return PagePosKind.Start;
		else if(startPos.equals(pageWordCount)) return PagePosKind.End;
		else return PagePosKind.Middle;
	}
}
