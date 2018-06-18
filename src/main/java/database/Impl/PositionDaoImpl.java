package database.Impl;

import database.dao.PositionDao;
import database.entity.UserEntity;
import pro.nextbit.telegramconstructor.database.DataBaseUtils;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.database.DataTable;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.List;

public class PositionDaoImpl implements PositionDao {

    private DataBaseUtils utils;

    public PositionDaoImpl(DataSource source) {
        this.utils = new DataBaseUtils(source);
    }


    @Override
    public long getResepshen(int id_centres) {
        return utils.queryDataRec("SELECT * FROM position WHERE id_centre = ?", id_centres).getLong("user_id");
    }


    @Override
    public long getResepshenClining () {
        return utils.queryDataRec("SELECT * FROM position WHERE id = 7").getLong("user_id");
    }

    @Override
    public long getResepshnToId(int id) {
        return utils.queryDataRec("SELECT * FROM position WHERE id = ?", id).getLong("user_id");
    }

    @Override
    public DataTable getService() {
        return utils.query("SELECT * FROM position WHERE row_position NOTNULL " +
                "ORDER BY row_position"

        );
    }

    @Override
    public DataTable getReceptionist() {
        return utils.query("SELECT * FROM position WHERE row_position ISNULL ");
    }

    public void updateResepwn(long user, int id) {
        utils.update(
                "UPDATE position SET user_id = ? WHERE id = ?", user, id);
    }


    @Override
    public DataTable getServiceMember(int id) {
        return utils.query("SELECT service.id AS id_s, * FROM service INNER JOIN users " +
                "ON service.user_id = users.chat_id WHERE service.id_position =? ", id);
    }


    @Override
    public int insertService(long user_id, int id_position) {
        return (int) utils.updateForKeyId(
                "INSERT INTO service (user_id, id_position) " +
                        "VALUES (?,?)", user_id, id_position

        );
    }

    @Override
    public void deleteServiceMember(int id) {
        utils.update(
                "DELETE FROM service WHERE id=? ", id
        );
    }


}
