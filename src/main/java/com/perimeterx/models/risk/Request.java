package com.perimeterx.models.risk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.perimeterx.models.PXContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Request model
 * <p>
 * Created by shikloshi on 05/07/2016.
 */
public class Request {
    @JsonProperty("url")
    public String URL;

    @JsonProperty("headers")
    @JsonSerialize(using = HeadersSerializer.class)
    public List<Map.Entry<String, String>> Headers;

    @JsonProperty("socket_ip")
    private String socketIp;

    public static Request fromContext(PXContext context) {

        Request request = new Request();
        request.socketIp = context.getIp();
        request.URL = context.getFullUrl();
        request.Headers = new ArrayList<>(context.getHeaders().entrySet());
        return request;
    }
}

class HeadersSerializer extends StdSerializer<List<Map.Entry<String, String>>> {
    HeadersSerializer() {
        this(null);
    }

    private HeadersSerializer(Class<List<Map.Entry<String, String>>> t) {
        super(t);
    }

    @Override
    public void serialize(List<Map.Entry<String, String>> entries, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        if (entries != null) {
            for (Map.Entry<String, String> entry : entries) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("name", entry.getKey());
                jsonGenerator.writeStringField("value", entry.getValue());
                jsonGenerator.writeEndObject();
            }
        }
        jsonGenerator.writeEndArray();
    }
}