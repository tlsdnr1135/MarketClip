package com.seven.marketclip.chat.subpub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seven.marketclip.chat.domain.ChatMessages;
import com.seven.marketclip.chat.service.ChatMessageService;
import com.seven.marketclip.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;

    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 onMessage가 해당 메시지를 받아 처리한다.
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // redis에서 발행된 데이터를 받아 deserialize
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            // ChatMessage 객채로 맵핑
            ChatMessages roomMessage = objectMapper.readValue(publishMessage, ChatMessages.class);
            // Websocket 구독자에게 채팅 메시지 Send
            String parseString = Long.toString(roomMessage.getChatRoomId());
            messagingTemplate.convertAndSend("/sub/chat/room/" + parseString, roomMessage);
            chatMessageService.saveChatMessage(roomMessage);    //DB에 저장 API 5번
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}