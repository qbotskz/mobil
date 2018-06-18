package handling.impl;

import database.dao.UserDao;
import handling.AbstractHandle;

public class AskForCategoryHandle extends AbstractHandle {

    UserDao userDao = daoFactory.userDao();

    static long Biim = 359077504;

/*
    @Step(value = "A_admin", commandText = "/admin")
    public void A_admin() throws Exception {
        int user = userDao.UserList(chatId).size();

        if (user == 0) {
            clearMessage(
                    bot.sendMessage(new SendMessage()
                            .setText("Введите вашу имя для получения доступа")
                            .setChatId(chatId)
                    )
            );
        } else if (userDao.getByChatId(chatId).getId_access_level() == 2 || userDao.getByChatId(chatId).getId_access_level() == 1) {
            redirect("M_menu");
        }

        step = "A_registr";

    }


    @Step("A_registr")
    public void registr() throws Exception {

        clearMessage(
                bot.sendMessage(new SendMessage()
                        .setText("Введите вашу фамилию ")
                        .setChatId(chatId)
                )
        );


        setParam(Biim, "A_registr1").set("chatid", chatId).set("user_name", inputText);
        step = "A_registr1";

        setParam("A_registr1").set("chatid", chatId).set("user_name", inputText);
        step = "A_registr1";

    }


    @Step("A_registr1")
    public void A_registr1() throws Exception {

        UserEntity userEntity = new UserEntity();
        userEntity.setUser_name(param.getString("user_name"));
        userEntity.setUser_surname(inputText);
        userEntity.setId_access_level(2);
        userEntity.setChat_id(param.getLong("chatid"));

        userDao.insert(userEntity);

        bot.sendMessage(new SendMessage()
                .setText("Регистрация прошла успешно")
                .setChatId(chatId)
        );
        redirect("M_menu");


    }*/
}
