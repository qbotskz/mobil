package handling.impl;

import components.keyboard.KeyboardOld;
import database.dao.PositionDao;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

public class EditServiceHandle extends AbstractHandle {

    private PositionDao positionDao = daoFactory.positionDao();
    private int id;


    @Step(value = "editService", commandText = "\uD83D\uDCCC Смена исполнителя") //🔘 Главное меню
    public void editService() throws Exception {
        if (!hasAccess()){
            return;
        }
        IKeyboard kb = new IKeyboard();
        kb.next(2, 2, 2, 1);
        for (DataRec rec : positionDao.getService()) {
            kb.add(rec.getString("name_p"), Json.set("step", "editService2").set("id", rec.getInt("id")));
        }
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Выберите категорию для внесений изменений")
                .setChatId(chatId)
        ));
    }


    @Step(value = "editService2")
    public void receptionist2() throws Exception {
        id = queryData.getInt("id");

        IKeyboard kb = new IKeyboard();
        kb.next();
        for (DataRec rec : positionDao.getServiceMember(id)) {
            kb.add(rec.getString("user_name"), Json.set("step", "editService").set("id", rec.getInt("id")));
        }
        kb.add("Добавить сотрудника", Json.set("step", "addEmployee"));
        kb.add("Удалить сотрудника", Json.set("step", "deleteEmployee"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Выберите категорию для внесений изменений")
                .setChatId(chatId)
        ));
    }


    @Step(value = "addEmployee")
    public void addEmployee() throws Exception {
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Отправьте контакт сотрудника для добавления")
                .setChatId(chatId)
        ));
        step = "addEmployee2";
    }

    @Step(value = "addEmployee2")
    public void addEmployee2() throws Exception {

        if (update.getMessage().getContact() != null) {
            if (update.getMessage().getContact().getUserID() == null) {
                bot.sendMessage(new SendMessage()
                        .setText("Данные пользователь не зарегестрирован в телеграм")
                        .setChatId(chatId)
                        .enableHtml(true));
            } else {
                positionDao.insertService(update.getMessage().getContact().getUserID(), id);
                bot.sendMessage(new SendMessage()
                        .setText("Данные успешно изменены")
                        .setChatId(chatId)
                        .enableHtml(true));
            }

        } else {
            bot.sendMessage(new SendMessage()
                    .setText("Неверный формат данных")
                    .setChatId(chatId)
                    .enableHtml(true)
            );
        }

    }


    @Step(value = "deleteEmployee")
    public void deleteEmployee() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        for (DataRec rec : positionDao.getServiceMember(id)) {
            kb.add(rec.getString("user_name"), Json.set("step", "deleteEmployee2").set("id", rec.getInt("id_s")));
        }
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Выберите сотрудника для удаления")
                .setReplyMarkup(kb.generate())
                .setChatId(chatId)));
    }

    @Step(value = "deleteEmployee2")
    public void deleteEmployee2() throws Exception {
        positionDao.deleteServiceMember(queryData.getInt("id"));
        bot.sendMessage(new SendMessage()
                .setText("Сотрудник успешно удален")
                .setChatId(chatId));
        redirect("editService");
    }

}
