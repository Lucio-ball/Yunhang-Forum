package com.yunhang.forum.model.entity;

import com.yunhang.forum.model.enums.EventType;
import java.time.LocalDateTime;

/** 互动事件：由 ObservableEntity 产生，用于驱动通知。 */
public class Event {
    private final EventType type;
    private final User triggerUser;
    private final String message;
    private final LocalDateTime time;

    public Event(EventType type, User triggerUser, String message) {
        this.type = type;
        this.triggerUser = triggerUser;
        this.message = message;
        this.time = LocalDateTime.now();
    }

    public EventType getType() { return type; }
    public User getTriggerUser() { return triggerUser; }
    public String getMessage() { return message; }
    public LocalDateTime getTime() { return time; }
}

