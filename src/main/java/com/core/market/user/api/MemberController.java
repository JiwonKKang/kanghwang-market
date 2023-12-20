package com.core.market.user.api;

import com.core.market.common.Response;
import com.core.market.user.api.request.MemberCreateRequest;
import com.core.market.user.app.MemberService;
import com.core.market.user.domain.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Tag(name = "회원 API", description = "회원정보관련 API 입니다.")
    @PostMapping
    public Response<Void> createMember(@RequestBody MemberCreateRequest request, @AuthenticationPrincipal Member member) { //소셜로그인 이후 회원정보 추가 등록 API
        memberService.createMember(request, member.getEmail());
        return Response.success();
    }
}
