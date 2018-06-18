package handling.impl;

import components.keyboard.KeyboardOld;
import database.dao.PositionDao;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

public class EditReceptionistHandle extends AbstractHandle {

    private PositionDao positionDao = daoFactory.positionDao();
    private int id;


    @Step(value = "receptionist", commandText = "⚙ Смена ресепшн")
    public void receptionist() throws Exception {
        if (!hasAccess()){
            return;
        }
        IKeyboard kb = new IKeyboard();
        kb.next();
        for (DataRec rec : positionDao.getReceptionist()) {
            kb.add(rec.getString("name_p"), Json.set("step", "receptionist2").set("id", rec.getInt("id")));
        }
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Выберите на каком ресепшн вы хотите сменить сотрудника.")
                .setChatId(chatId)
        ));

    }


    @Step(value = "receptionist2")
    public void receptionist2() throws Exception {
        id = queryData.getInt("id");
        bot.sendMessage(new SendMessage()
                .setText("Отправьте контакты сотрудника для смены")
                .setChatId(chatId)
                .enableHtml(true)
        );
        step = "receptionist3";
    }

    @Step(value = "receptionist3")
    public void receptionist3() throws Exception {

        if (update.getMessage().getContact() != null) {
            positionDao.updateResepwn(update.getMessage().getContact().getUserID(), id);

            bot.sendMessage(new SendMessage()
                    .setText("Данные успешно изменены")
                    .setChatId(chatId)
                    .enableHtml(true)
            );
        } else {
            bot.sendMessage(new SendMessage()
                    .setText("Неверный формат данных")
                    .setChatId(chatId)
                    .enableHtml(true)
            );
        }

    }

}
