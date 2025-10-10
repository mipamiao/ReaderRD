package com.mipa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterContentPage {
	private String chapterId;
	private String id;
	private String data;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
