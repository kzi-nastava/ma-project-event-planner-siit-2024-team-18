package com.example.eventplanner.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class NotificationModel {
    private int id;
    private String title;
    private String content;
    private int itemId;
    private String notificationType;
    private LocalDateTime date;
    private boolean seen;

    public NotificationModel() {}

    public NotificationModel(int id, String title, String content, boolean seen, String notificationType, int itemId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.seen = seen;
        this.notificationType = notificationType;
        this.itemId = itemId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getNotificationType() { return notificationType; }
    public void setNotificationType(String notificationType) { this.notificationType = notificationType; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationModel that = (NotificationModel) o;
        return id == that.id &&
                seen == that.seen &&
                itemId == that.itemId &&
                Objects.equals(title, that.title) &&
                Objects.equals(content, that.content) &&
                Objects.equals(notificationType, that.notificationType) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, content, itemId, notificationType, date, seen);
    }

}
