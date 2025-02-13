package com.daruda.darudaserver.domain.user.controller;

import com.daruda.darudaserver.domain.user.dto.request.NicknameRequest;
import com.daruda.darudaserver.domain.user.dto.request.SignUpRequest;
import com.daruda.darudaserver.domain.user.dto.response.JwtTokenResponse;
import com.daruda.darudaserver.domain.user.dto.response.LoginResponse;
import com.daruda.darudaserver.domain.user.dto.response.SignUpSuccessResponse;
import com.daruda.darudaserver.domain.user.dto.response.UserInfo;
import com.daruda.darudaserver.domain.user.service.KakaoService;
import com.daruda.darudaserver.domain.user.service.UserService;
import com.daruda.darudaserver.global.auth.UserId;
import com.daruda.darudaserver.global.common.response.ApiResponse;
import com.daruda.darudaserver.global.error.code.SuccessCode;
import com.daruda.darudaserver.global.error.dto.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

import static com.daruda.darudaserver.global.error.code.SuccessCode.SUCCESS_REDIRECT;

@Slf4j
@RequestMapping("/api/v1/users")
@RestController
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;
    private final UserService userService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;


    @GetMapping("/kakao/login-url")
    public ResponseEntity<ApiResponse<String>> requestLogin(HttpServletResponse response) throws IOException {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + redirectUri;


        return ResponseEntity.ok(ApiResponse.ofSuccessWithData(location, SUCCESS_REDIRECT));
    }

    @PostMapping("/token")
    public ResponseEntity<SuccessResponse<LoginResponse>> postAuthenticationCode(@RequestHeader(value = "Authorization") String code){
        log.debug("CODE = "+code);
        UserInfo userInfo = kakaoService.getInfo(code);
        LoginResponse loginResponse = userService.oAuthLogin(userInfo);
        return ResponseEntity.ok(SuccessResponse.of(loginResponse,SuccessCode.SUCCESS_CREATE));
    }

    @PostMapping("/signup")
    public ResponseEntity<SuccessResponse<SignUpSuccessResponse>> signUp(@Valid @RequestBody SignUpRequest signUpRequest){
        SignUpSuccessResponse signUpSuccessResponse = userService.createUser(signUpRequest.email(), signUpRequest.nickname(),signUpRequest.positions());
        return ResponseEntity.ok(SuccessResponse.of(signUpSuccessResponse,SuccessCode.SUCCESS_LOGIN));
    }

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Long>> logOut(@UserId Long userId){
        Long returnedUserId = userService.deleteUser(userId);
        return  ResponseEntity.ok(SuccessResponse.of(returnedUserId,SuccessCode.SUCCESS_LOGOUT));
    }

    @PostMapping("/nickname")
    public ResponseEntity<SuccessResponse<Boolean>> checkDuplicate(@NotNull(message = "닉네임은 필수입력값입니다") @RequestParam("nickname")String nickName){
        boolean result = userService.isDuplicated(nickName);
        return ResponseEntity.ok(SuccessResponse.of(result,SuccessCode.SUCCESS_FETCH));
    }

    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<JwtTokenResponse>> regenerateToken(@UserId Long userId){
        JwtTokenResponse jwtTokenResponse = userService.reissueToken(userId);
        return ResponseEntity.ok(SuccessResponse.of(jwtTokenResponse,SuccessCode.SUCCESS_REISSUE));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<SuccessResponse<?>> withdrawUser(@UserId Long userId){
        userService.withdrawMe(userId);
        return ResponseEntity.ok(SuccessResponse.of(SuccessCode.SUCCESS_WITHDRAW));
    }

}
