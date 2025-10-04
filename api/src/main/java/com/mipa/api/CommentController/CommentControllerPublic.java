package com.mipa.api.CommentController;

import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.utils.PageRecord;
import com.mipa.common.vo.CommentAndUserInfoVO;
import com.mipa.service.api.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "api/public/comment", produces = "application/json")
public class CommentControllerPublic {
    @Autowired
    ICommentService commentService;

    @GetMapping(path = "/list")
    public ApiResponse<PageRecord<CommentAndUserInfoVO>> listComment(
            @RequestParam("bookId") String bookId,
            @RequestParam("chapterId") String chapterId,
            @RequestParam("pageNumber") Integer pageNumber,
            @RequestParam("pageSize") Integer pageSize
    ) {
        var res = commentService.listComment(bookId, chapterId, pageNumber, pageSize);
        if (res != null)
            return ApiResponse.success(res);
        return ApiResponse.unauthorized(null);
    }
}
