package handling.impl;

import database.dao.InfoDao;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

public class EditHollHandle extends AbstractHandle {

    InfoDao infoDao = daoFactory.infoDao();
    private int id;


    @Step(value = "editHoll", commandText = "\uD83C\uDFE2 Конференц-залы")
    public void editHoll() throws Exception {
        if (!hasAccess()){
            return;
        }
        IKeyboard kb = new IKeyboard();
        kb.next(2, 2);
        kb.add("Нұрсаулет-1 зал 1", Json.set("step", "editHoll").set("bc", 1));
        kb.add("Нұрсаулет-2 зал 1", Json.set("step", "editHoll").set("bc", 2));
        kb.add("Нұрсаулет-2 зал 2", Json.set("step", "editHoll").set("bc", 3));
        kb.add("Нұрсаулет-2 зал 3", Json.set("step", "editHoll").set("bc", 4));

        if (queryData.containsKey("bc")) {
            id = queryData.getInt("bc");
            DataRec rec = infoDao.getInfoHoll(queryData.getInt("bc"));
            try {
                clearMessageOnClick(bot.sendPhoto(new SendPhoto()
                        .setReplyMarkup(kb.generate())
                        .setCaption(rec.getString("text_ih") + "\nДля смены фото просто отправьте новое фото зала")
                        .setChatId(chatId)
                        .setPhoto(rec.getString("photo"))));
            } catch (TelegramApiException e) {
                clearMessageOnClick(bot.sendMessage(new SendMessage()
                        .setReplyMarkup(kb.generate())
                        .setText(rec.getString("text_ih") + "\nОшибка отправки фото\nДля смены фото просто отправьте новое фото зала")
                        .setChatId(chatId)
                ));
            }
        } else {
            bot.sendMessage(new SendMessage()
                    .setReplyMarkup(kb.generate())
                    .setText("Выберите зал для просмотра фото")
                    .setChatId(chatId)
            );
        }
        step = "editHoll2";

    }

    @Step(value = "editHoll2")
    public void editHoll2() throws Exception {

        if (update.getMessage().getPhoto() != null) {
            infoDao.updateHollPhoto(update.getMessage().getPhoto().get(2).getFileId(), id);
        } else {
        }
        bot.sendMessage(new SendMessage()
                .setText("Данные успешно изменены")
                .setChatId(chatId)
                .enableHtml(true)
        );
    }


}
