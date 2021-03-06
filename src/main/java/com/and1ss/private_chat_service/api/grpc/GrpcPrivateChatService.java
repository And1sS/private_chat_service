package com.and1ss.private_chat_service.api.grpc;

import com.and1ss.private_chat_service.*;
import com.and1ss.private_chat_service.exceptions.BadRequestException;
import com.and1ss.private_chat_service.model.AccountInfo;
import com.and1ss.private_chat_service.model.PrivateChat;
import com.and1ss.private_chat_service.model.PrivateMessage;
import com.and1ss.private_chat_service.services.PrivateChatMessageService;
import com.and1ss.private_chat_service.services.PrivateChatService;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@GRpcService
public class GrpcPrivateChatService extends GrpcPrivateChatServiceGrpc.GrpcPrivateChatServiceImplBase {
    private final GrpcUserServiceConnection grpcUserServiceConnection;
    private final PrivateChatService privateChatService;
    private final PrivateChatMessageService privateChatMessageService;

    @Autowired
    public GrpcPrivateChatService(
            GrpcUserServiceConnection grpcUserServiceConnection,
            PrivateChatService privateChatService,
            PrivateChatMessageService privateChatMessageService
    ) {
        this.grpcUserServiceConnection = grpcUserServiceConnection;
        this.privateChatService = privateChatService;
        this.privateChatMessageService = privateChatMessageService;
    }

    @Override
    @Transactional
    public void getAllChats(
            GrpcAccessTokenIncomingDTO request,
            StreamObserver<GrpcPrivateChatsDTO> responseObserver
    ) {
        final var userDto = grpcUserServiceConnection
                .authorizeUserByAccessToken(request.getToken());
        final var authorizedUser = AccountInfo.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .build();

        List<PrivateChat> privateChats =
                privateChatService.getAllPrivateChatsForUser(authorizedUser);

        final var grpcChats = privateChats.stream()
                .map(chat -> GrpcPrivateChatDTO.newBuilder()
                        .setId(chat.getId().toString())
                        .setUser1Id(chat.getUser1Id().toString())
                        .setUser2Id(chat.getUser2Id().toString())
                        .build()
                ).collect(Collectors.toList());

        final var grpcDto = GrpcPrivateChatsDTO.newBuilder()
                .addAllChats(grpcChats)
                .build();

        responseObserver.onNext(grpcDto);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional
    public void getChatById(
            GrpcChatRetrievalDTO request,
            StreamObserver<GrpcPrivateChatDTO> responseObserver
    ) {
        final var userDto = grpcUserServiceConnection
                .authorizeUserByAccessToken(request.getToken());
        final var authorizedUser = AccountInfo.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .build();

        final var chatId = UUID.fromString(request.getChatId());
        PrivateChat privateChat = privateChatService.getPrivateChatById(chatId, authorizedUser);

        final var grpcDto = GrpcPrivateChatDTO.newBuilder()
                .setId(privateChat.getId().toString())
                .setUser1Id(privateChat.getUser1Id().toString())
                .setUser2Id(privateChat.getUser2Id().toString())
                .build();

        responseObserver.onNext(grpcDto);
        responseObserver.onCompleted();
    }

    @Override
    public void createPrivateChat(
            GrpcChatCreationDTO request,
            StreamObserver<GrpcPrivateChatDTO> responseObserver
    ) {
        try {
            final var userDto = grpcUserServiceConnection
                    .authorizeUserByAccessToken(request.getToken());
            final var authorizedUser = AccountInfo.builder()
                    .id(userDto.getId())
                    .name(userDto.getName())
                    .surname(userDto.getSurname())
                    .build();

            final var user2Id = UUID.fromString(request.getUserId());
            final var user2Dto = grpcUserServiceConnection.getUserById(user2Id);

            final var user2 = AccountInfo.builder()
                    .id(user2Dto.getId())
                    .name(user2Dto.getName())
                    .surname(user2Dto.getSurname())
                    .build();

            if (user2.equals(authorizedUser)) {
                throw new BadRequestException("User can not create private chats with himself");
            }

            PrivateChat toBeCreated = PrivateChat.builder()
                    .user1Id(authorizedUser.getId())
                    .user2Id(user2.getId())
                    .build();


            log.info("Trying to create private chat: " + authorizedUser.getName() + "-" + user2.getName());
            PrivateChat createdChat = privateChatService
                    .createPrivateChat(toBeCreated, authorizedUser);

            final var grpcDto = GrpcPrivateChatDTO.newBuilder()
                    .setId(createdChat.getId().toString())
                    .setUser1Id(createdChat.getUser1Id().toString())
                    .setUser2Id(createdChat.getUser2Id().toString())
                    .build();

            responseObserver.onNext(grpcDto);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void getAllMessagesForChat(
            GrpcChatRetrievalDTO request,
            StreamObserver<GrpcPrivateMessagesRetrievalDTO> responseObserver
    ) {

        final var messages = getAllMessagesForChat(request);

        final var messagesDto = messages.stream()
                .map(message -> GrpcPrivateMessageRetrievalDTO.newBuilder()
                        .setId(message.getId().toString())
                        .setAuthorId(message.getAuthorId().toString())
                        .setChatId(message.getChatId().toString())
                        .setContents(message.getContents())
                        .setCreatedAt(message.getCreatedAt().toString())
                        .build()
                ).collect(Collectors.toList());

        final var grpcDto = GrpcPrivateMessagesRetrievalDTO.newBuilder()
                .addAllMessages(messagesDto)
                .build();

        responseObserver.onNext(grpcDto);
        responseObserver.onCompleted();
    }

    @Transactional
    public List<PrivateMessage> getAllMessagesForChat(GrpcChatRetrievalDTO request) {
        final var userDto = grpcUserServiceConnection
                .authorizeUserByAccessToken(request.getToken());
        final var authorizedUser = AccountInfo.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .build();

        final var chatId = UUID.fromString(request.getChatId());
        PrivateChat privateChat = privateChatService.getPrivateChatById(chatId, authorizedUser);

        return privateChatMessageService.getAllMessages(privateChat, authorizedUser);
    }

    @Override
    public void createMessage(
            GrpcPrivateMessageCreationDTO request,
            StreamObserver<GrpcPrivateMessageRetrievalDTO> responseObserver
    ) {
        try {
            final var userDto = grpcUserServiceConnection
                    .authorizeUserByAccessToken(request.getToken());
            final var authorizedUser = AccountInfo.builder()
                    .id(userDto.getId())
                    .name(userDto.getName())
                    .surname(userDto.getSurname())
                    .build();

            final var chatId = UUID.fromString(request.getChatId());
            PrivateChat privateChat = privateChatService.getPrivateChatById(chatId, authorizedUser);

            final var messageToCreate = PrivateMessage.builder()
                    .authorId(authorizedUser.getId())
                    .chatId(chatId)
                    .contents(request.getContents())
                    .build();

            final var createdMessage = privateChatMessageService
                    .addMessage(privateChat, messageToCreate, authorizedUser);

            final var grpcDto = GrpcPrivateMessageRetrievalDTO.newBuilder()
                    .setId(createdMessage.getId().toString())
                    .setAuthorId(createdMessage.getAuthorId().toString())
                    .setChatId(createdMessage.getChatId().toString())
                    .setContents(createdMessage.getContents())
                    .setCreatedAt(createdMessage.getCreatedAt().toString())
                    .build();

            responseObserver.onNext(grpcDto);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}
