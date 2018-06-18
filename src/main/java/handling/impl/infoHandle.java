package handling.impl;

import components.keyboard.KeyboardOld;
import database.dao.InfoDao;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

public class infoHandle extends AbstractHandle {

    InfoDao infoDao = daoFactory.infoDao();

    @Step(value = "info", commandText = "\uD83D\uDCCB О нас")
    public void info() throws Exception {
        DataRec rec = infoDao.getInfo();
        KeyboardOld keyboard = new KeyboardOld();
        keyboard.next();
        keyboard.addButton("\uD83D\uDCDE Контакты");
        keyboard.addButton("\uD83D\uDD19 Назад");

        bot.sendPhoto(new SendPhoto()
                .setChatId(chatId)
                .setPhoto((String) rec.get("foto_in")));

        bot.sendMessage(new SendMessage()
                .setReplyMarkup(keyboard.generate())
                .setText(rec.get("text_in").toString())
                .setChatId(chatId)
                .enableHtml(true));
    }


    @Step(value = "news", commandText = "\uD83D\uDCF0 Новости")
    public void news() throws Exception {
        DataRec rec = infoDao.getNews();

        bot.sendMessage(new SendMessage()
                .setText(rec.get("text_n").toString())
                .setChatId(chatId)
                .enableHtml(true));
    }


    @Step(value = "updateInfo")
    public void rejectEmpl() throws Exception {
        if (!hasAccess()){
            return;
        }
        bot.sendMessage(new SendMessage()
                .setText("Введите новый текст или отправьте фото")
                .setChatId(chatId)
                .enableHtml(true));
        step = "updateInfo2";
    }

    @Step(value = "updateInfo2")
    public void updateInfo2() throws Exception {
        if (!hasAccess()){
            return;
        }
        if (update.getMessage().getPhoto() != null) {
            infoDao.updatePhoto(update.getMessage().getPhoto().get(2).getFileId());
        } else {
            infoDao.updateText(inputText);
        }
        bot.sendMessage(new SendMessage()
                .setText("Данные успешно изменены")
                .setChatId(chatId)
                .enableHtml(true)
        );
        step = "updateInfo2";
    }


    @Step(value = "contacts", commandText = "\uD83D\uDCDE Контакты")
    public void contacts() throws Exception {
        DataRec rec = infoDao.getInfo();
        bot.sendMessage(new SendMessage()
                .setText(rec.get("contacts").toString())
                .setChatId(chatId)
                .enableHtml(true));
    }


    //-------------------панель админа----------------------

    @Step(value = "editInfo", commandText = "\uD83D\uDEE0 Изменить сообщения") //🔘 Главное меню
    public void editInfo() throws Exception {
        if (!hasAccess()){
            return;
        }
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("\uD83D\uDD8D️ Изменить сообщение «О нас»", Json.set("step", "updateInfo"));
        kb.add("\uD83D\uDD8D ️Изменить сообщение «Новости»", Json.set("step", "updateNews"));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Главное меню")
                .setChatId(chatId)
        ));

    }


    @Step(value = "updateNews")
    public void updateNews() throws Exception {
        if (!hasAccess()){
            return;
        }
        bot.sendMessage(new SendMessage()
                .setText("Введите новый текст или отправьте фото")
                .setChatId(chatId)
                .enableHtml(true));
        step = "updateNews2";
    }

    @Step(value = "updateNews2")
    public void updateNews2() throws Exception {
        if (!hasAccess()){
            return;
        }
        if (update.getMessage().getPhoto() != null) {
            infoDao.updateNewsPhoto(update.getMessage().getPhoto().get(2).getFileId());
        } else {
            infoDao.updateNewsText(inputText);
        }
        bot.sendMessage(new SendMessage()
                .setText("Данные успешно изменены")
                .setChatId(chatId)
                .enableHtml(true)
        );
    }


}
