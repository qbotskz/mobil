package database.Impl;

import database.dao.UserDao;
import database.entity.UserEntity;
import org.springframework.dao.DataAccessException;
import pro.nextbit.telegramconstructor.accesslevel.AccessLevel;
import pro.nextbit.telegramconstructor.accesslevel.AccessLevelMap;
import pro.nextbit.telegramconstructor.database.DataBaseUtils;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.database.DataTable;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private DataBaseUtils utils;

    public UserDaoImpl(DataSource source) {
        this.utils = new DataBaseUtils(source);
    }


    @Override
    public UserEntity getByChatId(long chatId) {
        return utils.queryForObject("select * from users where chat_id = ?",
                new Object[] { chatId }, this::mapper
        );
    }

    @Override
    public List<UserEntity> UserList(long chatId) {
        return utils.query("select * from users where chat_id = ?",
                new Object[] { chatId }, this::mapper
        );
    }

    @Override
    public List<UserEntity> UserList(String phoneMatch) {
        try {
            return utils.query("SELECT * FROM users WHERE users.phone ILIKE '%' || ? || '%' ORDER BY user_name",
                    new Object[] { phoneMatch }, this::mapper
            );
        } catch (DataAccessException e) {
            return null;
        }
    }


    @Override
    public DataTable UserListAll() {
        return utils.query("select * ,ROW_NUMBER() OVER(ORDER BY users.id) from users INNER JOIN category " +
                        "ON users.id_category = category.id");
    }

    @Override
    public int insert(UserEntity user) {
        return (int) utils.updateForKeyId(
                "INSERT INTO users (chat_id, user_name, id_category, " +
                        "id_access_level,phone) " +
                        "VALUES (?,?,?,?,?)",
                user.getChat_id(), user.getUser_name(), user.getId_category(),
                user.getId_access_level(),user.getPhone()
        );
    }


    private UserEntity mapper(ResultSet rs, int index) throws SQLException {

        UserEntity u = new UserEntity();
        u.setId(rs.getInt("id"));
        u.setId_access_level(rs.getInt("id_access_level"));
        u.setChat_id(rs.getLong("chat_id"));
        u.setUser_name(rs.getString("user_name"));
        u.setId_position(rs.getInt("id_position"));
        u.setPhone(rs.getString("phone"));
        u.setId_category(rs.getInt("id_category"));
        return u;
    }
}
