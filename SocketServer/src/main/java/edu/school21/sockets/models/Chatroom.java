package edu.school21.sockets.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Chatroom {
    private Long id;
    private String name;
    private User owner;
    private List<Message> messages;

    public Chatroom() {
        this.id = null;
        this.name = null;
        this.owner = null;
        this.messages = new LinkedList<>();
    };

    public Chatroom(Long id) {
        this.id = id;
        this.name = null;
        this.owner = null;
        this.messages = new LinkedList<>();
    };

    public Chatroom(Long id, String name, User owner) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.messages = new LinkedList<>();
    }

    public Chatroom(Long id, String name, User owner, List<Message> messages)  {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getOwner() {
        return owner;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Chatroom chatroom = (Chatroom) o;
        return id.equals(chatroom.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Chatroom: {\nid = " + (id != null ? id : null) + "\n" +
            "ChatroomName = " + name + ", Owner = " + (owner != null ? owner.getName() : "null") + "\n}";
    }
}