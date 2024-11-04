package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Chatroom;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.sql.DataSource;

@Component("messagesRepositoryJdbc")
@Scope("singleton")
public class MessagesRepositoryJdbcImpl implements MessagesRepository {
    private static final String QUERY_FIND_BY_ID = "SELECT * FROM messages WHERE id = ?;";
    private static final String QUERY_FIND_LAST_N_FROM_ROOM = "SELECT * FROM (SELECT * FROM messages WHERE chatroom_id = ? ORDER BY id DESC LIMIT ?) ORDER BY id;";
    private static final String QUERY_SAVE = "INSERT INTO messages (author_id, chatroom_id, text, date_time) VALUES (?, ?, ?, ?);";
	private static final String QUERY_UPDATE = "UPDATE messages SET author_id = ?, chatroom_id = ?, text = ?, date_time = ? WHERE id = ?;";
	private static final String QUERY_DELETE = "DELETE FROM messages WHERE id = ?;";
    private final JdbcTemplate jdbcTemplate;
    private final UsersRepository usersRepository;
    private final ChatroomsRepository chatroomsRepository;

    @Autowired
    public MessagesRepositoryJdbcImpl(
        @Qualifier("hikariDataSource") DataSource dataSource,
        @Qualifier("usersRepositoryJdbc") UsersRepository usersRepository,
        @Qualifier("chatroomsRepositoryJdbc") ChatroomsRepository chatroomsRepository) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.usersRepository = usersRepository;
        this.chatroomsRepository = chatroomsRepository;
    }	
	
	@Override
    public Optional<Message> findById(Long id) {
        Message foundMsg = jdbcTemplate.queryForObject(QUERY_FIND_BY_ID, new MessageRowMapper(usersRepository, chatroomsRepository), id);
        return foundMsg != null ? Optional.of(foundMsg) : Optional.empty();
    }

    @Override
    public List<Message> getRoomStory(Long room_id, int amount) {
        List<Message> lastMessages = jdbcTemplate.query(QUERY_FIND_LAST_N_FROM_ROOM, new MessageRowMapper(usersRepository, chatroomsRepository), room_id, amount);
        return lastMessages != null ? lastMessages : new LinkedList<>();
    }

	@Override
    public void save(Message message) {
        if (jdbcTemplate.update(QUERY_SAVE,
            message.getAuthor() != null ? message.getAuthor().getId() : null,
            message.getRoom() != null ? message.getRoom().getId() : null,
            message.getText(),
            message.getDateTime() != null ? Timestamp.valueOf(message.getDateTime()) : null) == 0) {
			    System.err.println("Can't save Message with id = " + message.getId());
		}
    }

	@Override
    public void update(Message message) {
        if (jdbcTemplate.update(QUERY_UPDATE, message.getAuthor() != null ? message.getAuthor().getId() : null,
            message.getRoom() != null ? message.getRoom().getId() : null,
            message.getText(),
            message.getDateTime() != null ? Timestamp.valueOf(message.getDateTime()) : null,
            message.getId()) == 0) {
                System.err.println("Can't update Message with id = " + message.getId());
        }
    }
	
	@Override
    public void delete(Long id) {
        if (jdbcTemplate.update(QUERY_DELETE, id) == 0) {
            System.err.println("Cant't delete/find Messaage with id = " + id);
        }
    }

    //  ROW MAPPER  //
    public class MessageRowMapper implements RowMapper<Message> {
        private final UsersRepository usersRepository;
        private final ChatroomsRepository chatroomsRepository;
    
        public MessageRowMapper(UsersRepository usersRepository, ChatroomsRepository chatroomsRepository) {
            this.usersRepository = usersRepository;
            this.chatroomsRepository = chatroomsRepository;
        }
        @Override
        public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
            Optional<User> author = usersRepository.findById(rs.getLong("author_id"));
            Optional<Chatroom> room = chatroomsRepository.findById(rs.getLong("chatroom_id"));
            if (author.isPresent() && room.isPresent()) {
                Message message = new Message();
                message.setId(rs.getLong("id"));
                message.setAuthor(author.get());
                message.setRoom(room.get());
                message.setText(rs.getString("text"));
                message.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
                return message;
            }
            return null;
        }
    }
}
