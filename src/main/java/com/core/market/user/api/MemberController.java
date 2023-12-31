package com.core.market.user.api;

import com.core.market.chat.app.ChatAlarmService;
import com.core.market.common.Response;
import com.core.market.user.api.request.MemberCreateRequest;
import com.core.market.user.api.response.UserResponse;
import com.core.market.user.app.MemberService;
import com.core.market.user.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "회원 API", description = "회원정보관련 API 입니다.")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ChatAlarmService chatAlarmService;

    @PatchMapping
    @Operation(summary = "회원 추가정보 생성")
    public Response<Void> updateMember(@RequestBody MemberCreateRequest request,
                                       @AuthenticationPrincipal Member member) { //소셜로그인 이후 회원정보 추가 등록 API
        memberService.updateMember(request, member);
        return Response.success();
    }

    @GetMapping
    @Operation(summary = "회원 정보 가져오기")
    public Response<UserResponse> getMemberInfo(@AuthenticationPrincipal Member member) {
        return Response.success(UserResponse.from(member));
    }

    @DeleteMapping("/logout")
    @Operation(summary = "회원 로그아웃")
    public Response<Void> logout(HttpServletRequest request, @AuthenticationPrincipal Member member) {
        memberService.logout(request, member.getEmail());
        return Response.success("로그아웃 성공");
    }

    @GetMapping("/subscribe")
    @Operation(summary = "채팅방 알림을 위한 SSE 구독")
    public SseEmitter subscribe(@AuthenticationPrincipal Member member) {
        return chatAlarmService.connectAlarm(member.getId());
    }
}
