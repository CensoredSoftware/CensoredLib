package com.censoredsoftware.library.bukkitutil;

import com.censoredsoftware.library.util.TimeUtil;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class ChatRecorder {
    private Map<Long, String> lines; // Format: <System.currentTimeMillis, Message>
    private Player player;
    private Listener listener;
    private boolean recording;

    public void start(Player player) {
        this.player = player;
        listener = new ChatListener();
        lines = Maps.newTreeMap();
        recording = true;
    }

    public List<String> stop() {
        HandlerList.unregisterAll(this.listener);

        return Lists.newArrayList(Collections2.transform(lines.entrySet(), new Function<Map.Entry<Long, String>, String>() {
            @Override
            public String apply(Map.Entry<Long, String> entry) {
                return ChatColor.GRAY + "[" + TimeUtil.getTimeTagged(entry.getKey(), true) + " ago]" + entry.getValue();
            }
        }));
    }

    public Listener getListener() {
        return listener;
    }

    public boolean isRecording() {
        return recording;
    }

    class ChatListener implements Listener {
        @EventHandler(priority = EventPriority.MONITOR)
        private void onChatEvent(AsyncPlayerChatEvent event) {
            if (event.getRecipients().contains(player)) lines.put(System.currentTimeMillis(), event.getFormat());
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onAllianceChatEvent(AsyncPlayerChatEvent event) {
            if (event.getRecipients().contains(player)) lines.put(System.currentTimeMillis(), event.getMessage());
        }
    }

    public static ChatRecorder startRecording(Player player, Plugin plugin) {
        ChatRecorder recorder = new ChatRecorder();
        recorder.start(player);
        plugin.getServer().getPluginManager().registerEvents(recorder.getListener(), plugin);
        return recorder;
    }
}
