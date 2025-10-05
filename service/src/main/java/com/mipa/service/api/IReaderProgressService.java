package com.mipa.service.api;

import com.mipa.common.dto.readprogressDTO.ReaderProgressDTO;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.ReaderProgressVO;

public interface IReaderProgressService {
    void updateReadProgress(ReaderProgressDTO dto, String userId);

    PageRecord<ReaderProgressVO> getReaderProgres(String userId, Integer pageNumber, Integer pageSize);

    void delReaderProgress(String userId, String readerprogressId);
}
