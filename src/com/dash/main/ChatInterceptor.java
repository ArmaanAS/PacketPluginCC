package com.dash.main;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.tinyprotocol.Reflection;
import com.comphenix.tinyprotocol.Reflection.FieldAccessor;
import com.comphenix.tinyprotocol.Reflection.MethodInvoker;
import com.comphenix.tinyprotocol.TinyProtocol;

import io.netty.channel.Channel;

public class ChatInterceptor extends JavaPlugin {
	private FieldAccessor<String> chatMessage = Reflection.getField("{nms}.PacketPlayInChat", String.class, 0);
	
	private Class<?> chatOutClass = Reflection.getMinecraftClass("PacketPlayOutChat");
	private Class<Object> icbc = Reflection.getUntypedClass("{nms}.IChatBaseComponent");
	private FieldAccessor<Object> baseCompenent = Reflection.getField(chatOutClass, icbc, 0);
	private MethodInvoker addText = Reflection.getMethod(icbc, "a", String.class);

	private TinyProtocol protocol;
	
	
	@Override
    public void onEnable() {
		protocol = new TinyProtocol(this) {
			
			@Override
			public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
				
				if (chatMessage.hasField(packet)) {
					String arrivedMsg = chatMessage.get(packet) + " Arrived";
					chatMessage.set(packet, arrivedMsg);
				}

				return super.onPacketInAsync(sender, channel, packet);
			}
			
			@Override
			public Object onPacketOutAsync(Player sender, Channel channel, Object packet) {
				
				if (chatOutClass.isInstance(packet)) {
					Object baseComponent = baseCompenent.get(packet);
					addText.invoke(baseComponent, " Sent");
				}
				
				return super.onPacketInAsync(sender, channel, packet);
			}
			
		};
    }
}
