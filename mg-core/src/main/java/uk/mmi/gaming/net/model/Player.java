package uk.mmi.gaming.net.model;

public class Player {
	private String name;
	private boolean ready = false;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean value) {
		ready = value;
	}

	@Override
	public String toString() {
		return "Player [name=" + name + ", ready=" + ready + "]";
	}
}
