package com.web.socket.service.Impl;

import com.web.socket.dto.request.ContactCreationRequest;
import com.web.socket.dto.request.DeleteContactRequest;
import com.web.socket.dto.response.ContactResponse;
import com.web.socket.dto.SingleProfileDTO;
import com.web.socket.entity.ChatRoom;
import com.web.socket.entity.Contact;
import com.web.socket.entity.RoomType;
import com.web.socket.entity.User;
import com.web.socket.exception.BadRequestException;
import com.web.socket.exception.ResourceNotFoundException;
import com.web.socket.repository.ChatRoomRepository;
import com.web.socket.repository.UserRepository;
import com.web.socket.service.ContactService;
import com.web.socket.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public List<ContactResponse> getContactList() {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        List<ContactResponse> contactList = authenticatedUser.getContacts().stream().map(contact -> {
                    SingleProfileDTO singleProfileDTO = SingleProfileDTO
                            .builder()
                            .roomType("PRIVATE")
                            .id(contact.getUser().getId())
                            .name(contact.getName())
                            .username(contact.getUser().getUsername())
                            .status(contact.getUser().getStatus())
                            .bio(contact.getUser().getBio())
                            .avatar(contact.getUser().getAvatar())
                            .build();
                    return ContactResponse
                            .builder()
                            .contactId(contact.getId())
                            .chatRoomId(contact.getChatRoom().getId())
                            .roomInfo(singleProfileDTO)
                            .build();
                }
        ).toList();

        return contactList;
    }

    @Override
    @Transactional
    public ContactResponse createContact(ContactCreationRequest contactCreationRequest) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        String contactUsername = contactCreationRequest.getUsername();
        String contactName = contactCreationRequest.getName();

        if (contactUsername == null || contactName == null)
            throw new BadRequestException("Username or name must not be null");

        if(contactUsername.equals(username)) {
            throw new BadRequestException("You cannot add yourself as contact");
        }

        User contactUser = userRepository.findByUsername(contactUsername)
                .orElseThrow(() -> new ResourceNotFoundException("The username doesn't exist"));

        boolean existed = authenticatedUser.getContacts().stream()
                .anyMatch(contact -> contact.getUser().getId().equals(contactUser.getId()));
        if (existed)
            throw new BadRequestException("Contact existed");

        Query query = new Query();
        query.addCriteria(Criteria.where("roomType")
                .is("PRIVATE")
                .and("members").size(2)
                .all(
                        new ObjectId(authenticatedUser.getId()),
                        new ObjectId(contactUser.getId()))
                );

        ChatRoom chatRoom = mongoTemplate.findOne(query, ChatRoom.class);
        if (chatRoom == null)
            chatRoom = ChatRoom
                    .builder()
                    .groupAvatar("https://static.vecteezy.com/system/resources/thumbnails/036/280/651/small_2x/default-avatar-profile-icon-social-media-user-image-gray-avatar-icon-blank-profile-silhouette-illustration-vector.jpg")
                    .roomType(RoomType.PRIVATE)
                    .members(List.of(authenticatedUser, contactUser))
                    .build();

        Contact contact = Contact.builder()
                .name(contactName)
                .user(contactUser)
                .chatRoom(chatRoom)
                .build();

        contactUser.getChatRooms().add(chatRoom);
        authenticatedUser.getChatRooms().add(chatRoom);
        authenticatedUser.getContacts().add(contact);

        chatRoomRepository.save(chatRoom);
        userRepository.save(authenticatedUser);
        userRepository.save(contactUser);

        return ContactResponse
                .builder()
                .contactId(contact.getId())
                .chatRoomId(chatRoom.getId())
                .roomInfo(SingleProfileDTO
                        .builder()
                        .roomType("PRIVATE")
                        .id(contactUser.getId())
                        .name(contactName)
                        .username(contactUsername)
                        .status(contactUser.getStatus())
                        .bio(contactUser.getBio())
                        .avatar(contactUser.getAvatar())
                        .build())
                .build();
    }

    @Override
    @Transactional
    public void deleteContact(DeleteContactRequest deleteContactRequest) {
        Authentication authentication = SecurityUtils.getAuthentication();
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User authenticatedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid credential"));

        authenticatedUser.getContacts()
                .removeIf(contact -> contact.getId().equals(deleteContactRequest.getContactId()));
        userRepository.save(authenticatedUser);
    }
}
