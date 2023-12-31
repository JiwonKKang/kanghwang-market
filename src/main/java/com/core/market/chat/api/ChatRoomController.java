package com.core.market.chat.api;

import com.core.market.chat.api.request.ChatMessage;
import com.core.market.chat.api.response.ChatHistoryResponse;
import com.core.market.chat.api.response.ChatRoomResponse;
import com.core.market.chat.app.ChatRoomService;
import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.common.Response;
import com.core.market.common.util.JwtTokenUtil;
import com.core.market.user.domain.Member;
import com.core.market.user.domain.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-rooms")
@Tag(name = "채팅방 API입니다.")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token) {
        chatRoomService.sendAndSaveChatHistory(message, token);
    }

    @PostMapping("/{postId}")
    @Operation(summary = "채팅방 생성", description = "채팅을 시작할때 호출하는 API입니다.")
    public Response<Long> startChat(@PathVariable Long postId,
                                    @AuthenticationPrincipal Member member) {
        return Response.success(chatRoomService.startChat(postId, member.getId()));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "채팅 기록 불러오기")
    public Response<List<ChatHistoryResponse>> getChatHistories(@PathVariable Long roomId,
                                                                @AuthenticationPrincipal Member member) {
        return Response.success(chatRoomService.getChatRoomHistories(roomId, member.getId()));
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지않은 전체 메시지 수 불러오기", description = "유저의 전체 읽지않은 메시지 수 API입니다.")
    public Response<Integer> getTotalUnreadChatCount(@AuthenticationPrincipal Member member) {
        return Response.success(chatRoomService.getTotalUnreadChatHistoryCount(member));
    }

    @GetMapping
    @Operation(summary = "유저의 채팅방 모두 불러오기", description = "유저가 판매 또는 구매하는 채팅방을 모두 불러옵니다.")
    public Response<List<ChatRoomResponse>> getChatRooms(@AuthenticationPrincipal Member member) {

        return Response.success(chatRoomService.getChatRooms(member));
    }

    @PatchMapping("/{roomId}")
    @Operation(summary = "채팅방 유저의 상태 OUT 변경", description = "채팅방을 나갈 때 호춣하는 API입니다.")
    public Response<Void> disconnectChatRoom(@PathVariable Long roomId, @AuthenticationPrincipal Member member) {
        chatRoomService.disconnectChatRoom(roomId, member);
        return Response.success();
    }

}
