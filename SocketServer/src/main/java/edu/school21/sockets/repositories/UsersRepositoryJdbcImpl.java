package edu.school21.sockets.repositories;

import edu.school21.sockets.models.User;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.LinkedList;
import java.util.List;

@Component("usersRepositoryJdbc")
@Scope("singleton")
public class UsersRepositoryJdbcImpl implements UsersRepository {
	private static final String QUERY_FIND_BY_ID = "SELECT * FROM users WHERE id = ?;";
	private static final String QUERY_FIND_BY_NAME = "SELECT * FROM users WHERE name = ?;";
	private static final String QUERY_FIND_ALL = "SELECT * FROM users;";
	private static final String QUERY_SAVE = "INSERT INTO users(id, name, password) VALUES(?, ?, ?);";
	private static final String QUERY_UPDATE = "UPDATE users SET name = ?, password = ? WHERE id = ?;";
	private static final String QUERY_DELETE = "DELETE FROM users WHERE id = ?;";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersRepositoryJdbcImpl(@Qualifier("hikariDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
    public Optional<User> findById(Long id) {
		User foundUser = jdbcTemplate.query(QUERY_FIND_BY_ID, new Object[]{id}, new BeanPropertyRowMapper<>(User.class))
			.stream().findFirst().orElse(null);
        return Optional.of(foundUser);
    }
	
	@Override
    public List<User> findAll() {
        List<User> allUsers = jdbcTemplate.query(QUERY_FIND_ALL, new BeanPropertyRowMapper<>(User.class));
        return allUsers != null ? allUsers : new LinkedList<>();
    }
	
	@Override
    public void save(User entity) {
        if (jdbcTemplate.update(QUERY_SAVE, entity.getId(), entity.getName(), entity.getPassword()) == 0) {
			System.err.println("Can't save User with id = " + entity.getId());
		}
    }
	
	@Override
    public void update(User entity) {
        if (jdbcTemplate.update(QUERY_UPDATE, entity.getName(), entity.getPassword(), entity.getId()) == 0) {
            System.err.println("Can't update User with id = " + entity.getId());
        }
    }
	
	@Override
    public void delete(Long id) {
        if (jdbcTemplate.update(QUERY_DELETE, id) == 0) {
            System.err.println("Cant't delete/find User with id = " + id);
        }
    }
	
	@Override
    public Optional<User> findByName(String name) {
        User foundUser = jdbcTemplate.query(QUERY_FIND_BY_NAME, new Object[]{name}, new BeanPropertyRowMapper<>(User.class))
			.stream().findFirst().orElse(null);
		return Optional.ofNullable(foundUser);
    }

}