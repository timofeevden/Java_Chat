package edu.school21.sockets.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.school21.sockets.models.Message;

import java.lang.reflect.Type;

public class MessageSerializer implements JsonSerializer<Message> {
	@Override
	public JsonElement serialize(Message message, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject result = new JsonObject();
        result.addProperty("author", (message.getAuthor() != null ? message.getAuthor().getName() : null));
        result.addProperty("text", (message.getText() != null ? message.getText() : null));
		return result;
	}
}
