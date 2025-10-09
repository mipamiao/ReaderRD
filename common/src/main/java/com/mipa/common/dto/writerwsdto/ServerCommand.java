package com.mipa.common.dto.writerwsdto;

import com.mipa.common.utils.ChapterContentInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerCommand {
	 private Integer index;
	 private String newId;
	 private String data;
	 public ServerCommand(Integer index, String newId){
		 this.index = index;
		 this.newId = newId;
	 }
	public ServerCommand(Integer index, String newId, String data){
		this.index = index;
		this.newId = newId;
		this.data = data;
	}
	private ChapterContentInfo contentInfo;
}
