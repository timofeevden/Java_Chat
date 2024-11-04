package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import java.util.List;
import java.util.LinkedList;

@Component("chatroomsRepositoryJdbc")
@Scope("singleton")
public class ChatroomsRepositoryJdbcImpl implements ChatroomsRepository {
    private static final String QUERY_FIND_BY_ID = "SELECT * FROM chatrooms WHERE id = ?;";
    private static final String QUERY_SAVE = "INSERT INTO chatrooms (id, name, owner_id) VALUES (?, ?, ?);";
	private static final String QUERY_UPDATE = "UPDATE chatrooms SET name = ?, owner_id = ? WHERE id = ?;";
	private static final String QUERY_DELETE = "DELETE FROM chatrooms WHERE id = ?;";
    private static final String QUERY_FIND_ALL = "SELECT * FROM chatrooms;";
    private final JdbcTemplate jdbcTemplate;
    private final UsersRepository usersRepository;

    @Autowired
    public ChatroomsRepositoryJdbcImpl(
        @Qualifier("hikariDataSource") DataSource dataSource,
        @Qualifier("usersRepositoryJdbc") UsersRepository usersRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.usersRepository = usersRepository;
    }	
	
	@Override
    public Optional<Chatroom> findById(Long id) {
        Chatroom foundRoom = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID, new ChatroomsRowMapper(usersRepository), id);
        return foundRoom != null ? Optional.of(foundRoom) : Optional.empty();
    }

    @Override
    public List<Chatroom> findAll() {
        List<Chatroom> allRooms = jdbcTemplate.query(QUERY_FIND_ALL, new ChatroomsRowMapper(usersRepository));
        return allRooms != null ? allRooms : new LinkedList<>();
    }

	@Override
    public void save(Chatroom room) {
        if (jdbcTemplate.update(QUERY_SAVE,
            room.getId(),
            room.getName(),
            room.getOwner() != null ? room.getOwner().getId() : null) == 0) {
			    System.err.println("Can't save Chatroom with id = " + room.getId());
		}
    }

	@Override
    public void update(Chatroom room) {
        if (jdbcTemplate.update(QUERY_UPDATE,
            room.getName(),
            room.getOwner() != null ? room.getOwner().getId() : null,
            room.getId()) == 0) {
                System.err.println("Can't update Chatroom with id = " + room.getId());
        }
    }
	
	@Override
    public void delete(Long id) {
        if (jdbcTemplate.update(QUERY_DELETE, id) == 0) {
            System.err.println("Cant't delete/find Chatroom with id = " + id);
        }
    }

    //  ROW MAPPER  //
    public class ChatroomsRowMapper implements RowMapper<Chatroom> {
        private final UsersRepository usersRepository;

        public ChatroomsRowMapper(UsersRepository usersRepository) {
            this.usersRepository = usersRepository;
        }
        
        @Override
        public Chatroom mapRow(ResultSet rs, int rowNum) throws SQLException {
            Optional<User> owner = usersRepository.findById(rs.getLong("owner_id"));
            if (owner.isPresent()) {
                Chatroom Chatroom = new Chatroom();
                Chatroom.setId(rs.getLong("id"));
                Chatroom.setName(rs.getString("name"));
                Chatroom.setOwner(owner.get());
                return Chatroom;
            }
            return null;
        }
    }
}

