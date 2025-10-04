package com.mipa.service.api;

import com.mipa.common.commentdto.CommentDTO;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.CommentAndUserInfoVO;

public interface ICommentService {
    CommentAndUserInfoVO addComment(CommentDTO dto, String userId);

    boolean delComment(String userId, String commentId);

    PageRecord<CommentAndUserInfoVO> listComment(String bookId, String chapterId, Integer pageNumber, Integer pageSize);
}
