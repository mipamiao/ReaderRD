package com.mipa.mapper;

import com.mipa.model.BookTag;
import com.mipa.model.ChapterContentPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ChapterContentPageMapper {
	 Optional<ChapterContentPage> selectById(@Param("id") String id);

	List<ChapterContentPage> selectAll();

	int insert(ChapterContentPage chapterContentPage);

	int update(ChapterContentPage chapterContentPage);

	int delete(@Param("id") String id);


	int insertBatch(@Param("pages") List<ChapterContentPage> pages);

	int updateBatch(@Param("pages") List<ChapterContentPage> pages);

	int deleteBatch(@Param("ids") List<String> ids);
}
