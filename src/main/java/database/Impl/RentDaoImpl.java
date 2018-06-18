package database.Impl;

import database.dao.RentDao;
import database.entity.TaskEntity;
import pro.nextbit.telegramconstructor.database.DataBaseUtils;
import pro.nextbit.telegramconstructor.database.DataTable;

import javax.sql.DataSource;
import java.sql.Timestamp;

public class RentDaoImpl implements RentDao {

    private DataBaseUtils utils;

    public RentDaoImpl(DataSource source) {
        this.utils = new DataBaseUtils(source);
    }


    @Override
    public int insert(String text_of, long user_id, Timestamp data_creat) {
        return (int) utils.updateForKeyId(
                "INSERT INTO rent_office (text_of, user_id, data_creat) " +
                        "VALUES (?,?,?)", text_of, user_id, data_creat

        );
    }

    @Override
    public int insertHoll(String text_hol, long user_id, Timestamp datebegin, Timestamp deadline) {
        return (int) utils.updateForKeyId(
                "INSERT INTO rent_holl (text_hol, user_id, datebegin, deadline) " +
                        "VALUES (?,?,?,? )", text_hol, user_id, datebegin, deadline

        );
    }

    @Override
    public void deleteHoll(int id) {
        utils.update(
                "DELETE FROM rent_holl WHERE id=? ", id
        );
    }



    @Override
    public DataTable getOfficeList() {
        return utils.query("SELECT * FROM rent_office");
    }

    @Override
    public DataTable getHollList() {
        return utils.query("SELECT * FROM rent_holl");
    }
}
