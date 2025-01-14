package com.daruda.darudaserver.domain.community.controller;

import com.daruda.darudaserver.domain.community.dto.req.BoardCreateAndUpdateReq;
import com.daruda.darudaserver.domain.community.dto.res.BoardRes;
import com.daruda.darudaserver.domain.community.service.BoardService;
import com.daruda.darudaserver.domain.tool.dto.res.ToolScrapRes;
import com.daruda.darudaserver.global.auth.UserId;
import com.daruda.darudaserver.global.common.response.ApiResponse;
import com.daruda.darudaserver.global.error.code.SuccessCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/boards")
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createBoard(
            @UserId Long userId,
            @ModelAttribute @Valid final BoardCreateAndUpdateReq boardCreateAndUpdateReq,
            @RequestPart(value = "images", required = false)  @Size(max=5) List<MultipartFile> images){

        BoardRes boardRes = boardService.createBoard(userId, boardCreateAndUpdateReq, images);
        return ResponseEntity.ok(ApiResponse.ofSuccessWithData(boardRes,SuccessCode.SUCCESS_CREATE));
    }

    @PatchMapping("/{board-id}")
    public ResponseEntity<ApiResponse<?>> updateBoard(
            @UserId Long userId,
            @PathVariable(name="board-id") final Long boardId,
            @ModelAttribute @Valid final BoardCreateAndUpdateReq boardCreateAndUpdateReq,
            @RequestPart(value = "images", required = false)  @Size(max=5) List<MultipartFile> images){
        BoardRes boardRes = boardService.updateBoard(userId , boardId , boardCreateAndUpdateReq,images);
        return ResponseEntity.ok(ApiResponse.ofSuccessWithData(boardRes,SuccessCode.SUCCESS_UPDATE));
    }

    @GetMapping("/board/{board-id}")
    public ResponseEntity<ApiResponse<?>> getBoard(@PathVariable(name="board-id") final Long boardId){
        BoardRes boardRes = boardService.getBoard(boardId);
        return ResponseEntity.ok(ApiResponse.ofSuccessWithData(boardRes,SuccessCode.SUCCESS_FETCH));
    }

    @PostMapping("/{board-id}/scrap")
    public ResponseEntity<ApiResponse<?>> postToolScrap(@UserId final Long userId, @PathVariable(name="board-id") final Long toolId){
        ToolScrapRes toolScrapRes = toolService.postToolScrap(userId, toolId);
        return ResponseEntity.ok(ApiResponse.ofSuccessWithData(toolScrapRes, SuccessCode.SUCCESS_CREATE));
    }


    @DeleteMapping("/{board-id}")
    public ResponseEntity<ApiResponse<?>> deleteBoard(
            @UserId Long userId,
            @PathVariable(name="board-id") final Long boardId){
        boardService.deleteBoard(userId,boardId);
        return ResponseEntity.ok(ApiResponse.ofSuccess(SuccessCode.SUCCESS_DELETE));
    }
}
