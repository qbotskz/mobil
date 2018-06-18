package database.Impl;

import database.dao.AdminDao;
import database.entity.AdminEntity;
import org.springframework.dao.DataAccessException;
import pro.nextbit.telegramconstructor.database.DataBaseUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminDaoImpl implements AdminDao {

    private DataBaseUtils utils;

    public AdminDaoImpl(DataSource source) {
        this.utils = new DataBaseUtils(source);
    }


    @Override
    public AdminEntity getByChatId(long chatId) {
        try {
            return utils.queryForObject("SELECT * FROM admin WHERE chat_id = ?",
                    new Object[]{chatId}, this::mapper
            );
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public int insert(AdminEntity adminEntity) {
        utils.query("INSERT INTO admin VALUES (?,?)", adminEntity.getChatId(), adminEntity.getComment());
        return 0;
    }

    @Override
    public List<AdminEntity> AdminListAll() {
        try {
            return utils.query("SELECT * FROM admin ORDER BY comment", this::mapper);
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    public void delete(int chatId) {
        utils.update("DELETE FROM admin WHERE chat_id=?", chatId);
    }


    private AdminEntity mapper(ResultSet rs, int index) throws SQLException {
        AdminEntity u = new AdminEntity();
        u.setChatId(rs.getInt(1));
        u.setComment(rs.getString(2));
        return u;
    }
}
