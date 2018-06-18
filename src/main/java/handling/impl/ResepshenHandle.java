package handling.impl;

import database.dao.PositionDao;
import database.dao.TaskDao;
import database.dao.UserDao;
import database.entity.UserEntity;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResepshenHandle extends AbstractHandle {

    private TaskDao taskDao = daoFactory.taskDao();
    private PositionDao positionDao = daoFactory.positionDao();
    private UserDao userDao = daoFactory.userDao();
    private int id;
    private long resepwn;
    private long empl_id;


    @Step(value = "resepwn")
    public void resepwn() throws Exception {
        id = queryData.getInt("id");
        bot.sendMessage(new SendMessage()
                .setText("Ваша заявка успешно отправлена \n Заявка № " + id)
                .setChatId(chatId)
                .enableHtml(true)
        );
        redirect("goTask");
    }

    @Step(value = "goTask")
    public void goTask() throws Exception {
        if (id == 0) {
            id = queryData.getInt("id");
        }
        DataRec rec = taskDao.getTask(id);
        if (taskDao.getTaskOnly(id).getInt("id_position") == 15) {
            resepwn = positionDao.getResepshenClining();
        } else {
            resepwn = positionDao.getResepshen(rec.getInt("id_centres"));
        }
        StringBuilder sb = viewTask(rec);
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("Выбрать исполнителя", Json.set("step", "ChooseEmpl").set("id", id));
        kb.add("Отклонить", Json.set("step", "reject").set("id", id));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText(sb.toString())
                .setChatId(resepwn)
                .enableHtml(true)
        ));
    }


    @Step(value = "reject")
    public void reject() throws Exception {
        id = queryData.getInt("id");
        bot.sendMessage(new SendMessage()
                .setText("Введите текст пояснение к отклонению")
                .setChatId(chatId)
                .enableHtml(true)
        );
        step = "reject2";
    }

    @Step(value = "reject2")
    public void reject2() throws Exception {

        taskDao.updatClarification("Сотрудник: " + inputText, id);
        taskDao.updatStatus(6, id);
        DataRec rec = taskDao.getTask(id);
        if (taskDao.getTaskOnly(id).getInt("id_position") == 15) {
            resepwn = positionDao.getResepshenClining();
        } else {
            resepwn = positionDao.getResepshen(rec.getInt("id_centres"));
        }
        StringBuilder sb = viewTask(rec);
        bot.sendMessage(new SendMessage()
                .setText("Заявка № " + id + "отклонена")
                .setChatId(chatId)
                .enableHtml(true)
        );

        bot.sendMessage(new SendMessage()
                .setText("Заявка № отклонена\n" + sb.toString() + "Пояснение: " + inputText)
                .setChatId(rec.getLong("tenant_id"))
                .enableHtml(true)
        );
    }


    @Step(value = "ChooseEmpl")
    public void ChooseEmpl() throws Exception {
        id = queryData.getInt("id");
        IKeyboard kb = new IKeyboard();
        kb.next();
        for (DataRec rec : taskDao.getPosition(queryData.getInt("id"))) {
            try {
                kb.add(rec.getString("user_name"), Json.set("step", "empl").set("e_id", rec.getLong("chat_id")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        kb.add("Назад", Json.set("step", "goTask").set("id", queryData.getInt("id")));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Выберите исполнителя")
                .setChatId(chatId)
                .enableHtml(true)
        ));
    }

    @Step(value = "empl")
    public void empl() throws Exception {
        taskDao.updatStatus(2, id);
        taskDao.updatEmployee(queryData.getLong("e_id"), id);
        DataRec rec = taskDao.getTask(id);
        if (taskDao.getTaskOnly(id).getInt("id_position") == 15) {
            resepwn = positionDao.getResepshenClining();
        } else {
            resepwn = positionDao.getResepshen(rec.getInt("id_centres"));
        }
        UserEntity user = userDao.getByChatId(rec.getLong("employee_id"));
        StringBuilder sb = viewTask(rec);
        sb.append("Исполнитель : ").append(user.getUser_name());
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("Принять к работе", Json.set("step", "emplDoing").set("id", id));
        kb.add("Отклонить", Json.set("step", "rejectEmpl").set("id", id));
        kb.add("Выполнено", Json.set("step", "done").set("id", id));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText(sb.toString())
                .setChatId(queryData.getLong("e_id"))
                .enableHtml(true)
        ));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Заявка № " + id + " отправлена исполнителю")
                .setChatId(resepwn)
                .enableHtml(true)
        ));
    }


    @Step(value = "emplDoing")
    public void emplDoing() throws Exception {
        id = queryData.getInt("id");
        taskDao.updatStatus(3, id);
        DataRec rec = taskDao.getTask(id);
        if (taskDao.getTaskOnly(id).getInt("id_position") == 15) {
            resepwn = positionDao.getResepshenClining();
        } else {
            resepwn = positionDao.getResepshen(rec.getInt("id_centres"));
        }
        UserEntity user = userDao.getByChatId(rec.getLong("employee_id"));
        StringBuilder sb = viewTask(rec);
        sb.append("Исполнитель : " + user.getUser_name());
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Задания № " + id + " принято \n" + sb.toString())
                .setChatId(chatId)
                .enableHtml(true)
        ));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Задания № " + id + " принято сотрудником\n" + sb.toString())
                .setChatId(resepwn)
                .enableHtml(true)
        ));
    }


    @Step(value = "rejectEmpl")
    public void rejectEmpl() throws Exception {
        id = queryData.getInt("id");
        bot.sendMessage(new SendMessage()
                .setText("Введите текст пояснение к отклонению")
                .setChatId(chatId)
                .enableHtml(true)
        );

        step = "rejectEmpl2";
    }


    @Step(value = "rejectEmpl2")
    public void rejectEmpl2() throws Exception {
        taskDao.updatClarification("Сотрудник : " + inputText, id);
        taskDao.updatStatus(6, id);
        DataRec rec = taskDao.getTask(id);
        if (taskDao.getTaskOnly(id).getInt("id_position") == 15) {
            resepwn = positionDao.getResepshenClining();
        } else {
            resepwn = positionDao.getResepshen(rec.getInt("id_centres"));
        }
        UserEntity user = userDao.getByChatId(rec.getLong("employee_id"));
        StringBuilder sb = viewTask(rec);
        sb.append("Исполнитель : ").append(user.getUser_name());
        bot.sendMessage(new SendMessage()
                .setText("Заявка № " + id + "отклонена ")
                .setChatId(chatId)
                .enableHtml(true)
        );

        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("Выбрать исполнителя", Json.set("step", "ChooseEmpl").set("id", id));
        kb.add("Отклонить", Json.set("step", "reject").set("id", id));
        bot.sendMessage(new SendMessage()
                .setText("Заявка отклонена сотрудником\n" + sb.toString() + "\nПояснение: " + inputText)
                .setChatId(resepwn)
                .setReplyMarkup(kb.generate())
                .enableHtml(true)
        );
    }


    @Step(value = "done")
    public void done() throws Exception {
        id = queryData.getInt("id");
        taskDao.updatStatus(4, id);
        DataRec rec = taskDao.getTask(id);
        UserEntity user = userDao.getByChatId(rec.getLong("employee_id"));
        StringBuilder sb = viewTask(rec);
        sb.append("Исполнитель : ").append(user.getUser_name());
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("Подтвердить", Json.set("step", "done2").set("id", id));
        kb.add("Отклонить", Json.set("step", "rejectArend").set("id", id));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Задание № " + queryData.getInt("id") + " выполнено\n" + sb.toString())
                .setChatId(rec.getLong("tenant_id"))
                .setReplyMarkup(kb.generate())
                .enableHtml(true)
        ));
        bot.sendMessage(new SendMessage()
                .setText("Заявка отправлена на подтверждение")
                .setChatId(chatId)
                .enableHtml(true)
        );

    }


    @Step(value = "done2")
    public void done2() throws Exception {
        id = queryData.getInt("id");
        taskDao.updatStatus(7, id);
        bot.sendMessage(new SendMessage()
                .setText("Подтверждение отправлено")
                .setChatId(chatId)
                .enableHtml(true)
        );
    }


    @Step(value = "rejectArend")
    public void rejectArend() throws Exception {
        id = queryData.getInt("id");
        bot.sendMessage(new SendMessage()
                .setText("Введите текст пояснение к отклонению")
                .setChatId(chatId)
                .enableHtml(true)
        );

        step = "rejectArend2";
    }

    @Step(value = "rejectArend2")
    public void rejectArend2() throws Exception {
        taskDao.updatStatus(6, id);
        String text = "Арендатор :" + inputText;
        taskDao.updatClarification(text, id);
        bot.sendMessage(new SendMessage()
                .setText("Заявка отклонена")
                .setChatId(chatId)
                .enableHtml(true)
        );
    }


    private StringBuilder viewTask(DataRec rec) {
        Date daedline = rec.getDate("datadoing");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm | dd-MM-yyyy");
        String deadlines = dateFormat.format(daedline);
        StringBuilder sb = new StringBuilder();
        sb.append("Заявка № ").append(id).append("\n");
        sb.append("От : ").append(rec.getString("user_name")).append("\n");
        sb.append("Teл : ").append(rec.getString("phone")).append("\n");
        sb.append("Бизнес центр : ").append(rec.getString("name_c")).append("\n");
        sb.append("Сервис : ").append(rec.getString("name_p")).append("\n");
        sb.append("Дата : ").append(deadlines).append("\n");
        sb.append("Состояние : ").append(rec.getString("name_st")).append("\n");
        sb.append(rec.getString("text_t")).append("\n");

        return sb;
    }

}
