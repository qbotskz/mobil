package handling.impl;

import components.keyboard.KeyboardOld;
import database.dao.UserDao;
import database.entity.UserEntity;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.components.keyboard.Keyboard;
import pro.nextbit.telegramconstructor.stepmapping.Step;

public class MainMenuHandle extends AbstractHandle {

    private UserDao userDao = daoFactory.userDao();
    private UserEntity user;

    @Step(value = "M_menu", commandText = "\uD83D\uDD18 Главное меню") //🔘 Главное меню
    public void M_menu() throws Exception {
        KeyboardOld keyboard = new KeyboardOld();
        keyboard.next(2, 2, 1);
        keyboard.addButton("\uD83D\uDCCB О нас");
        keyboard.addButton("\uD83D\uDCF0 Новости");
        keyboard.addButton("\uD83D\uDCDA Заявки");
        keyboard.addButton("\uD83C\uDFE2 Отдел аренды");
        keyboard.addButton("\uD83D\uDDC2 Анкеты");
        bot.sendMessage(new SendMessage()
                .setReplyMarkup(keyboard.generate())
                .setText("Главное меню")
                .setChatId(chatId)
        );
    }

    @Step(value = "back", commandText = "\uD83D\uDD19 Назад")
    public void back() throws Exception {
        M_menu();
    }

    @Step(value = "A_start", commandText = "/start")
    public void A_start() throws Exception {

        if (userDao.UserList(chatId).size() != 0) {
            M_menu();
        } else {
            IKeyboard kb = new IKeyboard();
            kb.next();
            if (userDao.UserList(chatId).size() != 0) {
                M_menu();
            } else {
                kb.add("«Арендатор»", Json.set("step", "registr").set("id", 1));
                kb.add("«Потенциальный арендатор»", Json.set("step", "registr").set("id", 2));
                kb.add("«Сотрудник»", Json.set("step", "registr").set("id", 3));
            }

            clearMessageOnClick(bot.sendMessage(new SendMessage()
                    .setReplyMarkup(kb.generate())
                    .setText("Регистрация\nВыберите категорию")
                    .setChatId(chatId)
            ));
        }
    }


    @Step(value = "registr")
    public void registr() throws Exception {

        user = new UserEntity();
        user.setId_category(queryData.getInt("id"));
        user.setChat_id(chatId);
        bot.sendMessage(new SendMessage()
                .setText("Регистрация\nВведите свой данные (Фамилия Имя)")
                .setChatId(chatId)
        );
        step = "registr2";
    }

    @Step(value = "registr2")
    public void registr2() throws Exception {

        Keyboard kb = new Keyboard();
        kb.next();
        kb.add("Отправить свой номер").setRequestContact(true);
        user.setUser_name(inputText);
        bot.sendMessage(new SendMessage()
                .setText("Отправьте свой контакты нажав на кнопку ниже")
                .setChatId(chatId)
                .setReplyMarkup(kb.generate())
        );
        step = "registr3";
    }

    @Step(value = "registr3")
    public void registr3() throws Exception {

        user.setId_access_level(2);
        user.setPhone(message.getContact().getPhoneNumber());
        userDao.insert(user);
        M_menu();
    }

}
