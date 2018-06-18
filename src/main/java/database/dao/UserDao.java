package database.dao;

import database.entity.UserEntity;
import pro.nextbit.telegramconstructor.database.DataTable;

import java.util.List;

public interface UserDao {

    UserEntity getByChatId(long chatId);

    List<UserEntity> UserList(long chatId);

    List<UserEntity> UserList(String phoneMatch);

    int insert(UserEntity user);

    DataTable UserListAll();

}