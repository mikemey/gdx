package uk.mmi.gaming.net.model;

import java.util.ArrayList;

import uk.mmi.gaming.net.model.ServerMessage.ServerMessageType;

import com.esotericsoftware.kryo.Kryo;

public class ModelRegistrar {

	public static void registerDataObjects(Kryo kryo) {
		kryo.register(ArrayList.class);
		kryo.register(Player.class);
		kryo.register(Lobby.class);
		kryo.register(ServerMessageType.class);
		kryo.register(ServerMessage.class);
		kryo.register(PlayField.class);
		kryo.register(InputEvent.class);
	}
}
