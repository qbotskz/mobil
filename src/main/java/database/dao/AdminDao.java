package database.dao;

import database.entity.AdminEntity;
import pro.nextbit.telegramconstructor.database.DataTable;

import java.util.List;

public interface AdminDao {

    AdminEntity getByChatId(long chatId);

    int insert(AdminEntity adminEntity);

    List<AdminEntity> AdminListAll();

    void delete(int chatId);
}