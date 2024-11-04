package edu.school21.sockets.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    private Long id;
    private User author;
    private Chatroom room;
    private String text;
    private LocalDateTime dateTime;

    public Message() {
        this.id = null;
        this.author = null;
        this.room = null;
        this.text = null;
        this.dateTime = null;
    }

    public Message(User author, Chatroom room, String text) {
        this.author = author;
        this.room = room;
        this.text = text;
        this.dateTime = LocalDateTime.now();
    }

    public Message(Long id, User author, Chatroom room, String text) {
        this.id = id;
        this.author = author;
        this.room = room;
        this.text = text;
        this.dateTime = LocalDateTime.now();
    }

    public Message(Long id, User author, Chatroom room, String text, LocalDateTime dateTime) {
        this.id = id;
        this.author = author;
        this.room = room;
        this.text = text;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public Chatroom getRoom() {
        return room;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setRoom(Chatroom room) {
        this.room = room;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return ("Message : {\nid=" + id + 
            "\nauthor={id=" + author.getId() + ",login=\"" + author.getName() + "\"},\nroom={id=" + room.getId() + ",name=\"" + room.getName() +
            "\",creator=" + room.getOwner() + "},\ntext=\"" + text +
            "\",\ndateTime=" + (dateTime != null ? dateTime.toString() : null) + "\n}");
    }
}