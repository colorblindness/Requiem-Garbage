package group.skids.requiem.events;

import net.b0at.api.event.Event;
import net.b0at.api.event.types.EventType;
import net.minecraft.client.Minecraft;

public class UpdateEvent extends Event {
	private boolean onGround;
	private float yaw;
	private float pitch;
	private double y;
	private EventType eventType = EventType.POST;

	public UpdateEvent(final float yaw, final float pitch, final double y, final boolean onGround, final EventType eventType) {
		this.yaw = yaw;
		this.pitch = pitch;
		this.y = y;
		this.onGround = onGround;
		this.eventType = eventType;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

	public double getY() {
		return y;
	}

	public boolean isOnGround() {
		return onGround;
	}

	public void setYaw(float yaw) {
		Minecraft.getMinecraft().player.renderYawOffset = yaw;
		Minecraft.getMinecraft().player.rotationYawHead = yaw;
		this.yaw = yaw;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public EventType getEventType() {
		return eventType;
	}
}
