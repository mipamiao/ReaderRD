package com.mipa.mapper;

import com.mipa.common.vo.BookWithTagsVO;
import com.mipa.model.BookTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BookTagMapper {
    BookTag selectById(@Param("id") String id);

    List<BookTag> selectAll();

    int insert(BookTag bookTag);

    int update(BookTag bookTag);

    int delete(@Param("id") String id);

    int deleteByBookId(@Param("bookId") String bookId);

    int insertBatch(@Param("bookTags") List<BookTag> bookTags);



    List<BookWithTagsVO> selectBookAndTagsByBookIds(@Param("bookIds") List<String> bookIds);
    
}
