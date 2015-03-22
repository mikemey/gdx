package uk.mmi.gaming.net.model;

public class ServerMessage {
	private ServerMessageType type;
	private Object data;

	public enum ServerMessageType {
		COUNTDOWN, GAME_START, INITAL_MODEL;
	}

	public ServerMessage() {
		// for serialisation
	}

	public ServerMessage(ServerMessageType type) {
		this(type, null);
	}

	public ServerMessage(ServerMessageType type, Object data) {
		this.type = type;
		this.data = data;
	}

	public void setType(ServerMessageType type) {
		this.type = type;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public ServerMessageType getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

	@Override
	public String toString() {
		return "ServerMessage [type=" + type + ", data=" + data + "]";
	}
}
