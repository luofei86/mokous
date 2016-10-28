// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.action;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class WebDateObjectMapper extends ObjectMapper {

    public WebDateObjectMapper() {
        CustomSerializerFactory factory = new CustomSerializerFactory();
        factory.addGenericMapping(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date value, JsonGenerator jsonGenerator, SerializerProvider provider)
                    throws IOException, JsonProcessingException {
                jsonGenerator.writeString(String.valueOf(value.getTime()));
            }
        });
        this.setSerializerFactory(factory);
    }
}
