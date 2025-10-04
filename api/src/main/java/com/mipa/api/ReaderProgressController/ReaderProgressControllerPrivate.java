package com.mipa.api.ReaderProgressController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.dto.readprogressDTO.ReaderProgressDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.vo.ReaderProgressVO;
import com.mipa.service.api.IReaderProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/private/reader-progress", produces = "application/json")
public class ReaderProgressControllerPrivate {

    @Autowired
    IReaderProgressService readerProgressService;

    @PostMapping(path = "/update", consumes = "application/json")
    public ApiResponse<Boolean> updateReaderProgress(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody ReaderProgressDTO dto
            ){
        var res = readerProgressService.updateReadProgress(dto, userSecurity.getUserId());
        if(res)
            return ApiResponse.success(null);
        return ApiResponse.unauthorized(null);
    }

    @GetMapping(path = "/list")
    public ApiResponse<List<ReaderProgressVO>> listReaderProgress(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam(defaultValue = "0", name = "pageNumber") Integer pageNumber,
            @RequestParam(defaultValue = "10", name = "pageSize") Integer pageSize
    ){
        var res = readerProgressService.getReaderProgres(userSecurity.getUserId(), pageNumber, pageSize);
        if(res!=null)
            return ApiResponse.success(res.datas());
        else
            return ApiResponse.unauthorized(null);
    }

    @DeleteMapping(path = "del")
    public ApiResponse<Boolean> delReaderProgress(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam("readerProgressId") String readerProgressId
    ) {
        var res = readerProgressService.delReaderProgress(userSecurity.getUserId(), readerProgressId);
        if (res)
            return ApiResponse.success(null);
        return ApiResponse.unauthorized(null);
    }
}
