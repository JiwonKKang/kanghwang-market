package com.core.market.chat.app;

import com.core.market.chat.api.request.ChatMessage;
import com.core.market.chat.api.response.ChatHistoryResponse;
import com.core.market.chat.api.response.ChatRoomResponse;
import com.core.market.chat.domain.ChatHistory;
import com.core.market.chat.domain.ChatRoom;
import com.core.market.chat.domain.MessageType;
import com.core.market.chat.domain.repository.ChatHistoryRepository;
import com.core.market.chat.domain.repository.ChatRoomRepository;
import com.core.market.common.CustomException;
import com.core.market.common.ErrorCode;
import com.core.market.common.util.JwtTokenUtil;
import com.core.market.trade.domain.TradePost;
import com.core.market.trade.domain.repository.TradePostRepository;
import com.core.market.user.app.MemberService;
import com.core.market.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final TradePostRepository tradePostRepository;
    private final MemberService memberService;
    private final ChatHistoryRepository chatHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatAlarmService chatAlarmService;
    private final JwtTokenUtil jwtTokenUtil;


    public Long startChat(Long postId, Long buyerId) {
        TradePost post = getPostById(postId);

        Member seller = memberService.findById(post.getUser().getId());
        Member buyer = memberService.findById(buyerId);

        if (seller.equals(buyer)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "자신의 거래글로 채팅을 시작할 수 없습니다.");
        }

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByBuyerAndSellerAndPost(buyer, seller, post);

        if (chatRoom == null) { // 채팅 처음 시작
            ChatRoom firstRoom = ChatRoom.builder()
                    .seller(seller)
                    .buyer(buyer)
                    .post(post)
                    .build();

            chatRoomRepository.save(firstRoom);
            return firstRoom.getId();
        }

        return chatRoom.getId();
    }

    public List<ChatHistoryResponse> getChatRoomHistories(Long roomId, Long senderId) {

        ChatRoom chatRoom = getRoomById(roomId);
        updateUnreadCount(chatRoom, senderId); // 메시지를 가져온다는것은 여태까지 안읽은 채팅을 읽는다는 의미이기때문에 읽음 처리

        if (chatRoom.isAllUserIn()) { /* 채팅방에 이미 사람이 있었다면 입장하는 유저가 입장하면서 안읽었던 메세지들을 업데이트하지만
                                          기존 채팅방 유저의 화면에는 아직 안읽음으로 남아있기때문에 새로고침 메시지를 보냄*/
            sendRefreshMessge(roomId);
        }

        return chatRoom.getChatHistories().stream()
                .map(ChatHistoryResponse::of)
                .toList();
    }

    public void sendAndSaveChatHistory(ChatMessage message, String token) {



        ChatRoom chatRoom = getRoomById(message.roomId());
        Member sender = getSender(token);
        Member receiver = chatRoom.getOtherUser(sender); //TODO : 채팅은 안읽은 유저에게 SSE 이벤트 발송

        int unreadCount = chatRoom.isAllUserIn() ? 0 : 1;

        ChatHistory chatHistory = ChatHistory.of(
                chatRoom,
                sender,
                message.message(),
                unreadCount,
                message.createdAt()
        );

        chatHistoryRepository.save(chatHistory);

        if (unreadCount == 1) {
            chatAlarmService.send(receiver);
        }

        ChatMessage chatMessage = ChatMessage.of(MessageType.MESSAGE, chatHistory);
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.roomId(), chatMessage);
    }

    private Member getSender(String token) {
        String email = jwtTokenUtil.extractEmail(token.replace("Bearer ", ""));
        return memberService.findByEmail(email);
    }

    public int getTotalUnreadChatHistoryCount(Member member) {
        return chatHistoryRepository.getTotalUnreadChatCount(member.getId());
    }

    public List<ChatRoomResponse> getChatRooms(Member member) {

        List<ChatRoomResponse> chatRoomResponses = new ArrayList<>();
        List<ChatRoom> chatRooms = chatRoomRepository.findChatRoomsByMemberId(member.getId());

        for (ChatRoom chatRoom : chatRooms) {

            ChatHistoryResponse lastChatHistory = ChatHistoryResponse.of(chatHistoryRepository.findLastChatHistory(chatRoom.getId()));
            Member chatPartner = chatRoom.getBuyer().getId().equals(member.getId()) ? chatRoom.getSeller() : chatRoom.getBuyer(); // 채팅 상대
            int totalUnreadCount = chatHistoryRepository.findUnreadChatHistoriesCountInChatRoom(member.getId(), chatRoom.getId());

            chatRoomResponses.add(ChatRoomResponse.of(chatRoom.getId(), lastChatHistory, chatPartner, totalUnreadCount));
        }

        return chatRoomResponses;

    }

    @Transactional
    public void disconnectChatRoom(Long roomId, Member member) {
        ChatRoom chatRoom = getChatRoomByRoomId(roomId);
        if (chatRoom.getBuyer().getId().equals(member.getId())) {
            chatRoom.buyerOut();
        } else {
            chatRoom.sellerOut();
        }
    }

    public ChatRoom getChatRoomByRoomId(Long roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROOM_NOT_FOUND));
    }

    private void updateUnreadCount(ChatRoom chatRoom, Long senderId) {
        chatRoom.getChatHistories().stream()
                .filter(chatHistory -> !chatHistory.getSender().getId().equals(senderId) &&
                        chatHistory.getUnreadCount() == 1)
                .forEach(ChatHistory::readMessage);

        chatHistoryRepository.flush();
    }

    private void sendRefreshMessge(Long roomId) {
        ChatMessage refreshMessage = ChatMessage.builder()
                .messageType(MessageType.NOTICE)
                .roomId(roomId)
                .build();
        messagingTemplate.convertAndSend("/sub/chat/room/" + refreshMessage.roomId(), refreshMessage);
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
