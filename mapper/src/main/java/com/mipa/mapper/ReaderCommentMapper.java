package com.mipa.mapper;

import com.mipa.common.vo.CommentAndUserInfoVO;
import com.mipa.model.ReaderComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ReaderCommentMapper {
    Optional<ReaderComment> selectById(@Param("id") String id);

    List<ReaderComment> selectAll();

    int insert(ReaderComment comment);

    int update(ReaderComment comment);

    int delete(@Param("id") String id);

    List<CommentAndUserInfoVO> selectAllWithUserInfoBybookIdAndChapterId(@Param("bookId") String bookId, @Param("chapterId") String chapterId);

    Optional<CommentAndUserInfoVO> selectAllWithUserInfoById(@Param("id") String id);
}
