package edu.school21.sockets.models;

import java.util.Objects;

public class User {
	private Long id;
	private String name;
	private String password;

	public User() {
		this.id = null;
		this.name = null;
		this.password = null;
	}

	public User(Long id) {
		this.id = id;
		this.name = null;
		this.password = null;
	}
	
	public User(Long id, String name) {
		this.id = id;
		this.name = name;
		this.password = null;
	}

	public User(Long id, String name, String password) {
		this.id = id;
		this.name = name;
		this.password = password;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", name='" + name + "', password='" + password + "'}";
    }
	
}