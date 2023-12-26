package com.core.market.chat.app;

import com.core.market.chat.api.request.ChatMessage;
import com.core.market.chat.api.response.ChatResponse;
import com.core.market.chat.api.response.ChatRoomResponse;
import com.core.market.chat.domain.ChatHistory;
import com.core.market.chat.domain.ChatRoom;
import com.core.market.chat.domain.repository.ChatHistoryRepository;
import com.core.market.chat.domain.repository.ChatRoomRepository;
import com.core.market.chat.domain.repository.CurrentChatRoomInfo;
import com.core.market.chat.domain.repository.CurrentChatRoomInfoRepository;
import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.repository.TradePostRepository;
import com.core.market.user.app.MemberService;
import com.core.market.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final TradePostRepository tradePostRepository;
    private final MemberService memberService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final CurrentChatRoomInfoRepository chatRoomInfoRepository;


    public ChatRoomResponse startChat(Long postId, Long buyerId) {
        TradePost post = getPostById(postId);

        Member seller = memberService.findById(post.getUser().getId());
        Member buyer = memberService.findById(buyerId);

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByBuyerAndSellerAndPost(buyer, seller, post);

        if (chatRoom == null) { // 채팅 처음 시작
            ChatRoom firstRoom = ChatRoom.builder()
                    .seller(seller)
                    .buyer(buyer)
                    .post(post)
                    .build();
            chatRoomRepository.save(firstRoom);
            return ChatRoomResponse.from(firstRoom);
        }

        return ChatRoomResponse.from(chatRoom);
    }

    public ChatResponse getChatRoomHistories(Long roomId, Long senderId) {

        /*TODO: 접속 유저가 2명이라면 상대에게 채팅방 새로고침 메세지 보내기*/
        ChatRoom chatRoom = getRoomById(roomId);

        return ChatResponse.of(chatRoom, senderId);
    }

    public void saveChatHistory(ChatMessage message) {

        int unreadCount = isAllUserInChatRoom(message.roomId()) ? 0 : 1;

        ChatRoom chatRoom = getRoomById(message.roomId());
        Member member = memberService.findById(message.senderId());


        ChatHistory chatHistory = ChatHistory.of(
                chatRoom,
                member,
                message.message(),
                unreadCount,
                message.createdAt()
        );

        chatHistoryRepository.save(chatHistory);

        ChatMessage sendMessage = ChatMessage.of(message, unreadCount);
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.roomId(), sendMessage);

    }

    public void connectChatRoom(Long roomId, String email) {
        CurrentChatRoomInfo chatRoomInfo = CurrentChatRoomInfo.builder()
                .roomId(roomId)
                .email(email)
                .build();

        chatRoomInfoRepository.save(chatRoomInfo);
    }

    private boolean isAllUserInChatRoom(Long roomId) {
        return chatRoomInfoRepository.findByRoomId(roomId).size() == 2;
    }

    private ChatRoom getRoomById(Long roomId) {
        return chatRoomRepository.findById(roomId).orElseThrow(() ->
                new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }


    private TradePost getPostById(Long postId) {
        return tradePostRepository.findById(postId).orElseThrow(()
                -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

}
