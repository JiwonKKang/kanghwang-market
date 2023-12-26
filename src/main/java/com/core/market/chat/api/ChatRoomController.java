package com.core.market.chat.api;

import com.core.market.chat.api.request.ChatMessage;
import com.core.market.chat.api.response.ChatResponse;
import com.core.market.chat.api.response.ChatRoomResponse;
import com.core.market.chat.app.ChatRoomService;
import com.core.market.common.Response;
import com.core.market.user.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat-rooms")
@Tag(name = "채팅방 API입니다.")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        chatRoomService.saveChatHistory(message);
    }

    @PostMapping("/{postId}")
    @Operation(summary = "채팅방 생성", description = "채팅을 시작할때 호출하는 API입니다.")
    public Response<ChatRoomResponse> startChat(@PathVariable Long postId,
                                                @AuthenticationPrincipal Member member) {

        ChatRoomResponse res = chatRoomService.startChat(postId, member.getId());

        return Response.success(res);
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "채팅 기록 불러오기")
    public Response<ChatResponse> getChatHistories(@PathVariable Long roomId,
                                                   @AuthenticationPrincipal Member member) {
        ChatResponse res = chatRoomService.getChatRoomHistories(roomId, member.getId());
        return Response.success(res);
    }
}
