package com.web.socket;

//import com.web.socket.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;


@SpringBootApplication
public class SocketApplication implements CommandLineRunner {
	public static void main(String[] args) {
		SpringApplication.run(SocketApplication.class, args);
	}
//	@Autowired
//	TestRepository repository;

	@Autowired
	MongoTemplate mongoTemplate;
	@Override
	public void run(String... args) throws Exception {
//		Contact contact1 = Contact.builder().name("Phat").build();
//		Contact contact2 = Contact.builder().name("Le").build();
//
//	User user1 = User.builder()
//			.name("goc")
//			.username("gc")
//			.password("123").build();
//
//	User user2 = User.builder()
//			.name("goc")
//			.username("gc")
//			.password("123").build();
//	user1.getContacts().add(contact1);
//	user2.getContacts().add(contact2);
//	mongoTemplate.save(user, "user");
//	mongoTemplate.insert(user1);
//	mongoTemplate.insert(user2);
//	Query query = new Query();
//	query.addCriteria(Criteria.where("_id").is("677894ceb42c6874cfee252c"));
//	List<User> user1 = mongoTemplate.find(query, User.class);

//		Contact contact = new Contact();
//		contact.setName("hihi");
//		contact.setChatRoomId("1233");
//
//		User user = new User();
//		user.setName("Phat23");
//		user.setContacts(List.of(contact));
//		mongoTemplate.save(user, "user");

//		List<User> tests =  mongoTemplate.findAll(User.class);
//		Query query = new Query();
//		query.addCriteria(Criteria.where("voiceDetail.voiceNoteUrl").is("Https:12"));
//		User user1 = new User();
//		user1.setName("phat");
//		user1.setUsername("phat");
//		user1.setStatus(new User.UserStatus());
//		user1.setPassword("123");
//
//		User user2 = new User();
//		user2.setName("le");
//		user2.setUsername("le");
//		user2.setStatus(new User.UserStatus());
//		user2.setPassword("123");
//
//		mongoTemplate.insertAll(List.of(user1, user2));
//
//		ChatRoom chatRoom = new ChatRoom();
//		chatRoom.setRoomType(ChatRoom.RoomType.PRIVATE);
//		chatRoom.setMembers(List.of(user1.getId(), user2.getId()));
//
//		mongoTemplate.save(chatRoom);


//		Query query = new Query();
//		query.addCriteria(Criteria.where("_id").is("677812e17335aa07cc5971cc"));
//		List<ChatRoom> tests = mongoTemplate.find(query, ChatRoom.class);

//		Query query1 = new Query();
//		query1.addCriteria(Criteria.where("_id").is("6778d38f6a766a64806b0ec4"));
//		User trang = mongoTemplate.findOne(query1, User.class);
//
//		Query query2 = new Query();
//		query2.addCriteria(Criteria.where("_id").is("6778d7c991a9701a91d84303"));
//		User phat = mongoTemplate.findOne(query2, User.class);
//
//		ChatRoom chatRoom = ChatRoom.builder()
//				.roomType(ChatRoom.RoomType.PRIVATE).members(List.of(trang, phat))
//				.build();
//		mongoTemplate.insert(chatRoom);
//
//		Contact contact = Contact
//				.builder()
//				.name("Phat cua trang")
//				.user(phat)
//				.chatRoom(chatRoom)
//				.build();
//		trang.getContacts().add(contact);
//
//		trang.getChatRooms().add(chatRoom);
//		phat.getChatRooms().add(chatRoom);
//		mongoTemplate.save(phat);
//		mongoTemplate.save(trang);
	}
}














































