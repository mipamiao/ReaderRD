package com.mipa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mipa.common.Constant.ExMsg;
import com.mipa.common.dto.readprogressDTO.ReaderProgressDTO;
import com.mipa.common.exception.BizException;
import com.mipa.common.utils.CopyProperties;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.ReaderProgressVO;
import com.mipa.mapper.ChapterMapper;
import com.mipa.mapper.ReaderProgressMapper;
import com.mipa.model.Chapter;
import com.mipa.model.ReaderProgress;
import com.mipa.service.api.IReaderProgressService;
import com.mipa.utils.IdUtil;
import com.mipa.validate.VerifyRelationShip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReaderProgressService implements IReaderProgressService {

    @Autowired
    ReaderProgressMapper readerProgressMapper;

    @Autowired
    ChapterMapper chapterMapper;


    @Transactional
    @Override
    public void updateReadProgress(ReaderProgressDTO dto, String userId) {
        var vr = VerifyRelationShip.start()
                .verifyBookAndChapter(dto.getBookId(), dto.getChapterId(), chapterMapper)
                .verifyChapterIdAndOrder(dto.getChapterOrder(), dto.getChapterId(), chapterMapper);
        if (vr.isSucceed()) {
            var chapter = vr.get(Chapter.class);
            dto.setUserId(userId);
            var readerProgress = CopyProperties.run(dto, ReaderProgress.class);

            try{
                var oldReaderProgressOpt = readerProgressMapper.selectByUserIdAndBookId(userId, dto.getBookId());
                if (oldReaderProgressOpt.isPresent()) {
                    readerProgress.setId(oldReaderProgressOpt.get().getId());
                    readerProgressMapper.update(readerProgress);
                } else {
                    readerProgress.setId(IdUtil.uuid());
                    readerProgressMapper.insert(readerProgress);
                }
            }catch (DataIntegrityViolationException e){
                throw BizException.badRequest(ExMsg.DB_CONSTRAIN_FAILED);
            }


        }else{
            throw BizException.badRequest(ExMsg.Or(ExMsg.CHAPTER_BOOK_MISMATCH, ExMsg.CHAPTER_ID_ORDER_MISMATCH));
        }
    }

    @Override
    public PageRecord<ReaderProgressVO> getReaderProgres(String userId, Integer pageNumber, Integer pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        var readerProgressVOs = readerProgressMapper.selectAllAndBookByUserId(userId);
        var pageInfo = new PageInfo<>(readerProgressVOs);
        return PageRecord.of(pageInfo.getList(), pageInfo);
    }

    @Transactional
    @Override
    public void delReaderProgress(String userId, String readerprogressId) {
        var vr = VerifyRelationShip.start().verifyReaderProgressAndUser(userId, readerprogressId, readerProgressMapper);
        if (vr.isSucceed()) {
            readerProgressMapper.delete(readerprogressId);
        }else {
            throw BizException.badRequest(ExMsg.READPROGRESS_USER_MISMATCH);
        }
    }
}
