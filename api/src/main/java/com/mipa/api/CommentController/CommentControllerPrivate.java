package com.mipa.api.CommentController;


import com.mipa.auth.Security.UserSecurity;
import com.mipa.common.commentdto.CommentDTO;
import com.mipa.common.response.ApiResponse;
import com.mipa.common.vo.CommentAndUserInfoVO;
import com.mipa.service.UserService;
import com.mipa.service.api.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/private/comment", produces = "application/json")
public class CommentControllerPrivate {

    @Autowired
    ICommentService commentService;

    @PostMapping(path = "add", consumes = "application/json")
    public ApiResponse<CommentAndUserInfoVO> addComment(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestBody CommentDTO dto
    ) {
        var res = commentService.addComment(dto, userSecurity.getUserId());
        if (res != null)
            return ApiResponse.success(res);
        return ApiResponse.unauthorized(null);
    }

    @DeleteMapping(path = "del")
    public ApiResponse<Boolean> delComment(
            @AuthenticationPrincipal UserSecurity userSecurity,
            @RequestParam("commentId") String commentId
    ) {
        var res = commentService.delComment(userSecurity.getUserId(), commentId);
        if (res)
            return ApiResponse.success(true);
        return ApiResponse.unauthorized(false);
    }
}
