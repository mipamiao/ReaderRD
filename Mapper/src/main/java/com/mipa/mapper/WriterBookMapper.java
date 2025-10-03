package com.mipa.mapper;

import com.mipa.model.WriterBook;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WriterBookMapper {
    WriterBook selectById(@Param("id") String id);

    List<WriterBook> selectAll();

    int insert(WriterBook writerBook);

    int update(WriterBook writerBook);

    int delete(@Param("id") String id);
}
