package com.web.socket.service;

import com.web.socket.entity.Parent;
import com.web.socket.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class testService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ParentRepository parentRepository;

    @Transactional
    public void test() throws Exception {
        Parent p = Parent
                .builder()
                .userName("username_1")
                .build();
        parentRepository.save(p);
        throw new Exception("hih");
    }
}
