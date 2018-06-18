package database.dao;

import pro.nextbit.telegramconstructor.database.DataTable;

import java.sql.Timestamp;

public interface RentDao {

    int insert(String text_of, long user_id, Timestamp data_creat);

    int insertHoll(String text_hol, long user_id, Timestamp datebegin, Timestamp deadline) ;

    void deleteHoll(int id);

    DataTable getOfficeList();

    DataTable getHollList();
}
