package com.mipa.common.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapterContentInfo {
	private List<String> pageIds;
	private List<Integer> pageWordCounts;
}
