package com.example.runningweb.mongo;

import com.example.runningweb.mongo.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findMessageByRoomNumber(String roomNumber);


}
