package com.javabitecode.demo.repos;

import com.javabitecode.demo.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByTeg(String tag);
}
