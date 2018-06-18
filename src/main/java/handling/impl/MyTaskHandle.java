package handling.impl;

import database.dao.PositionDao;
import database.dao.TaskDao;
import database.dao.UserDao;
import database.entity.TaskEntity;
import database.entity.UserEntity;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.database.DataTable;
import pro.nextbit.telegramconstructor.stepmapping.Step;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyTaskHandle extends AbstractHandle {

    private TaskDao taskDao = daoFactory.taskDao();
    private PositionDao positionDao = daoFactory.positionDao();
    private UserDao userDao = daoFactory.userDao();


    @Step(value = "myTask")
    public void myTask() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        UserEntity user = userDao.getByChatId(chatId);
        DataTable list;
        String text = " У Вас нет активных заявок";

        if (user.getId_category() != 3 || chatId == positionDao.getResepshen(1) || chatId == positionDao.getResepshen(2)) {
            if (chatId == positionDao.getResepshen(1) || chatId == positionDao.getResepshen(2)) {
                list = taskDao.getMytasRes();
            } else {
                list = taskDao.getMytaskList(chatId);
            }
        } else {
            list = taskDao.getMytaskEmpl(chatId);
        }

        if (list.size() != 0) {
            for (DataRec task : list) {
                try {
                    kb.add(task.getString("text_t"), Json.set("step", "myTaskView").set("id", task.getInt("id")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            text = "Выберите заявку из списка";
        }
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText(text)
                .setReplyMarkup(kb.generate())
                .setChatId(chatId)
                .enableHtml(true)
        ));

    }

    @Step(value = "myTaskView")
    public void position() throws Exception {
        UserEntity user = null;
        DataRec rec = taskDao.getTask(queryData.getInt("id"));
        if (rec.getInt("id_status") != 1) {
            user = userDao.getByChatId(rec.getLong("employee_id"));
        }
        Date daedline = rec.getDate("datadoing");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm | dd-MM-yyyy");
        String deadlines = dateFormat.format(daedline);
        StringBuilder sb = new StringBuilder();
        sb.append("Заявка № ").append(queryData.getInt("id")).append("\n");
        sb.append("От : ").append(rec.get("user_name")).append("\n");
        sb.append("Teл : ").append(rec.get("phone")).append("\n");
        sb.append("Бизнес центр : ").append(rec.get("name_c")).append("\n");
        sb.append("Сервис : ").append(rec.get("name_p")).append("\n");
        sb.append("Дата : ").append(deadlines).append("\n");
        sb.append("Состояние : ").append(rec.get("name_st")).append("\n");
        sb.append(rec.getString("text_t")).append("\n");

        if (rec.getInt("id_status") != 1) {
            try {
                sb.append("Исполнитель : ").append(user.getUser_name()).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (rec.get("clarification") != null) {
            sb.append("Пояснения : ").append(rec.getString("clarification"));
        }


        IKeyboard kb = new IKeyboard();
        kb.next();

        UserEntity check = userDao.getByChatId(chatId);

        if (check.getId_category() != 3 || chatId == positionDao.getResepshen(1) || chatId == positionDao.getResepshen(2)) {

            if (chatId == positionDao.getResepshen(1) || chatId == positionDao.getResepshen(2)) {
                kb.add("Выбрать исполнителя", Json.set("step", "ChooseEmpl").set("id", queryData.getInt("id")));
                kb.add("Отклонить", Json.set("step", "reject").set("id", queryData.getInt("id")));
                clearMessageOnClick(
                        bot.sendMessage(new SendMessage()
                                .setText(sb.toString())
                                .setReplyMarkup(kb.generate())
                                .setChatId(chatId)
                                .enableHtml(true)
                        )
                );

            } else {
                if (rec.getInt("id_status") == 4) {
                    kb.add("Подтвердить", Json.set("step", "done2").set("id", queryData.getInt("id")));
                    kb.add("Отклонить", Json.set("step", "rejectArend").set("id", queryData.getInt("id")));
                }
                kb.add("Назад", Json.set("step", "myTask"));

                clearMessageOnClick(bot.sendMessage(new SendMessage()
                        .setText(sb.toString())
                        .setChatId(chatId)
                        .setReplyMarkup(kb.generate())
                        .enableHtml(true)
                ));
            }

        } else {

            if (rec.getInt("id_status") == 2 || rec.getInt("id_status") == 3) {

                if (rec.getInt("id_status") == 2) {
                    kb.add("Принять к работе", Json.set("step", "emplDoing").set("id", queryData.getInt("id")));
                    kb.add("Отклонить", Json.set("step", "rejectEmpl").set("id", queryData.getInt("id")));
                    kb.add("Выполнено", Json.set("step", "done").set("id", queryData.getInt("id")));
                }
                if (rec.getInt("id_status") == 3) {
                    kb.add("Отклонить", Json.set("step", "rejectEmpl").set("id", queryData.getInt("id")));
                    kb.add("Выполнено", Json.set("step", "done").set("id", queryData.getInt("id")));
                }
                kb.add("Назад", Json.set("step", "myTask"));

                clearMessageOnClick(
                        bot.sendMessage(new SendMessage()
                                .setText(sb.toString())
                                .setReplyMarkup(kb.generate())
                                .setChatId(chatId)
                                .enableHtml(true)
                        )
                );
            }
        }
    }


}
