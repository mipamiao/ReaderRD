package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.commentdto.CommentDTO;
import com.mipa.common.utils.CopyProperties;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.CommentAndUserInfoVO;
import com.mipa.mapper.ChapterMapper;
import com.mipa.mapper.ReaderCommentMapper;
import com.mipa.model.ReaderComment;
import com.mipa.service.api.ICommentService;
import com.mipa.utils.IdUtil;
import com.mipa.validate.VerifyRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService implements ICommentService {

    @Autowired
    ReaderCommentMapper commentMapper;

    @Autowired
    ChapterMapper chapterMapper;

    public CommentAndUserInfoVO addComment(CommentDTO dto, String userId){
        var vr = VerifyRelationShip.start().verifyBookAndChapter(dto.getBookId(), dto.getChapterId(), chapterMapper);
        if(vr.isSucceed()){
            var newItem = CopyProperties.run(dto, ReaderComment.class);
            newItem.setId(IdUtil.uuid());
            newItem.setUserId(userId);
            commentMapper.insert(newItem);
            return commentMapper.selectAllWithUserInfoById(newItem.getId()).get();
        }
        return null;
    }

    public boolean delComment(String userId, String commentId){
        var vr =  VerifyRelationShip.start().verifyCommentAndUserId(userId, commentId, commentMapper);
        if (vr.isSucceed()) {
            commentMapper.delete(commentId);
            return true;
        }
        return false;
    }

    public PageRecord<CommentAndUserInfoVO> listComment(String bookId, String chapterId, Integer pageNumber, Integer pageSize) {
        var vr = VerifyRelationShip.start().verifyBookAndChapter(bookId, chapterId, chapterMapper);
        if (vr.isSucceed()) {
            PageHelper.startPage(pageNumber, pageSize);
            var res = commentMapper.selectAllWithUserInfoBybookIdAndChapterId(bookId, chapterId);
            var pageInfo = new PageInfo<>(res);
            return PageRecord.of(pageInfo.getList(), pageInfo);
        }
        return null;
    }
}
