package com.web.socket.config;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
public class Converter implements org.springframework.core.convert.converter.Converter<String, Object> {

    @Override
    public Object convert(String vote) {
        if(ObjectId.isValid(vote))
            return new ObjectId(vote);
        return vote;
    }
}
