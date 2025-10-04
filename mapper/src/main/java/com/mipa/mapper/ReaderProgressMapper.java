package com.mipa.mapper;

import com.mipa.common.vo.ReaderProgressVO;
import com.mipa.model.ReaderProgress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReaderProgressMapper {
    Optional<ReaderProgress> selectById(@Param("id") String id);

    List<ReaderProgress> selectAll();

    int insert(ReaderProgress progress);

    int update(ReaderProgress progress);

    int delete(@Param("id") String id);

    Optional<ReaderProgress> selectByUserIdAndBookId(@Param("userId") String userId, @Param("bookId") String bookId);

    List<ReaderProgress> selectAllByUserId(@Param("userId") String userId);

    List<ReaderProgressVO> selectAllAndBookByUserId(@Param("userId") String userId);
}
