package com.web.socket;

//import com.web.socket.repository.ParentRepository;

import com.web.socket.repository.ChildRepository;
import com.web.socket.repository.ParentRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;


@SpringBootApplication
@EnableAsync
public class SocketApplication implements CommandLineRunner {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
		SpringApplication.run(SocketApplication.class, args);
	}


	@Autowired
	ParentRepository parentRepository;

	@Autowired
	ChildRepository childRepository;

	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	@Transactional
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
//		query2.addCriteria(Criteria.where("_id").is("67815fa293f3be2e66dc8c84"));
//		User minh = mongoTemplate.findOne(query2, User.class);
//
//		ChatRoom chatRoom = ChatRoom.builder()
//				.roomType(RoomType.PRIVATE).members(List.of(trang, minh))
//				.build();
//		mongoTemplate.insert(chatRoom);
//
//		Contact contact = Contact
//				.builder()
//				.name("Minh cua trang")
//				.user(minh)
//				.chatRoom(chatRoom)
//				.build();
//		trang.getContacts().add(contact);
//
//		trang.getChatRooms().add(chatRoom);
//		minh.getChatRooms().add(chatRoom);
//		mongoTemplate.save(minh);
//		mongoTemplate.save(trang);
//		Parent t1 = Parent.builder().build();
//		mongoTemplate.save(t1, "t1");

//		Parent t1 = Parent.builder().build();
//		Child s = Child.builder().name("child").build();
//
//		childRepository.save(s);
//		t1.setChild(s);
//		repository.save(t1);

//		childRepository.deleteById("677c8ce50302211b023b258b");
//		repository.findById("677c8ce50302211b023b258c");


//		Child child = childRepository.findById("677c8660f26e0545bdf20f52").get();
//		child.setName("New child");
//		childRepository.save(child);
//
//		repository.findById("677c8660f26e0545bdf20f53");

//		Child child = Child.builder().name("subbb").build();
//		Parent test = Parent.builder().child(child).build();
//		childRepository.save(child);
//		repository.save(test);
//		Parent tes = repository.findById("677c9326a8edf479396ea240").get();
//		tes.getChild().setName("b2o");
//		repository.save(tes);

//		Child c = new Child();
//		c.setUserName("username child");
//		c.setGroupName("groupname child");
//		parentRepository.save(c);

//		Parent p = new Parent();
//		p.setUserName("username child");
//		parentRepository.save(p);
//		Child child = childRepository.findById("67812cf6c804fa15bcd15ff0").orElse(null);
	}
}














































