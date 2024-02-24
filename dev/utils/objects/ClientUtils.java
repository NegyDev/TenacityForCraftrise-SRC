package dev.utils.objects;

import com.google.gson.JsonObject;

import cr.launcher.IChatComponent;
import cr.launcher.main.a;

public class ClientUtils {
	public static void displayChatMessage(String message) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", message);

        a.q.a(IChatComponent.Serializer.jsonToComponent(jsonObject.toString()),5L);
    }

}
