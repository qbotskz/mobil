package handling.impl;

import database.dao.PositionDao;
import database.dao.TaskDao;
import database.entity.TaskEntity;
import handling.AbstractHandle;
import org.joda.time.DateTime;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.TimePicker;
import pro.nextbit.telegramconstructor.components.datepicker.FullDatePicker;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TaskHandle extends AbstractHandle {

    private TaskDao taskDao = daoFactory.taskDao();
    private PositionDao positionDao = daoFactory.positionDao();
    private TaskEntity task;
    private long resepshn;
    private String text;
    private int id;


    @Step(value = "task", commandText = "\uD83D\uDCDA Заявки") //🔘 Главное меню
    public void task() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("\uD83D\uDD8B Оставить заявку", Json.set("step", "newTask"));
        kb.add("\uD83D\uDDC4 Мои заявки", Json.set("step", "myTask"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Выберите категорию")
                .setChatId(chatId)
        ));

    }


    @Step(value = "newTask", commandText = "\uD83D\uDD8B Оставить заявку")
    public void newTask() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("Нұрсаулет-1", Json.set("step", "bc").set("id", "1"));
        kb.add("Нұрсаулет-2", Json.set("step", "bc").set("id", "2"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Новая заявка\nВыберите бизнес центр")
                .setChatId(chatId)
        ));

    }

    @Step(value = "bc")
    public void bc() throws Exception {
        task = new TaskEntity();
        task.setId_centre(queryData.getInt("id"));
        resepshn = positionDao.getResepshen(queryData.getInt("id"));

        IKeyboard kb = new IKeyboard();
        kb.next(2, 2, 2, 1);
        for (DataRec rec : positionDao.getService()) {
            kb.add(rec.getString("name_p"), Json.set("step", "position").set("id", rec.getInt("id")));
        }
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Новая заявка\nВыберите вид сервиса")
                .setChatId(chatId)
        ));
    }

    @Step(value = "position")
    public void position() throws Exception {
        task.setId_position(queryData.getInt("id"));
        redirect("ChooseService");
    }

    @Step(value = "ChooseService")
    public void ChooseService() throws Exception {

        FullDatePicker datePicker = new FullDatePicker(queryData, step);
        DateTime date = datePicker.getDate(bot, " Укажите дату выполнения заявки!", message);

        DateTime times = new DateTime();
        TimePicker pickers = new TimePicker(queryData, step, times);
        times = pickers.getDateTime(bot, "Укажите время выполнения заявки!", message);

        Calendar cals = Calendar.getInstance();
        cals.set(Calendar.YEAR, date.getYear());
        cals.set(Calendar.MONTH, date.getMonthOfYear());
        cals.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        cals.set(Calendar.HOUR_OF_DAY, times.getHourOfDay());
        cals.set(Calendar.MINUTE, times.getMinuteOfHour());
        cals.set(Calendar.SECOND, 0);
        cals.set(Calendar.MILLISECOND, 0);
        DateTime deadlines = new DateTime(cals).minusMonths(1);
        task.setDatadoing(new Timestamp(deadlines.toDate().getTime()));


        DateTime dateNow = new DateTime();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, dateNow.getYear());
        cal.set(Calendar.MONTH, dateNow.getMonthOfYear());
        cal.set(Calendar.DAY_OF_MONTH, dateNow.getDayOfMonth());
        cal.set(Calendar.HOUR_OF_DAY, dateNow.getHourOfDay());
        cal.set(Calendar.MINUTE, dateNow.getMinuteOfHour());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        DateTime dateNows = new DateTime(cal).minusMonths(1);
        task.setDataadd(new Timestamp(dateNows.toDate().getTime()));


        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Новая заявка\nНапишите номер офиса")
                .setChatId(chatId)
                .enableHtml(true)

        ));

        step = "ofiice";

    }


    @Step(value = "ofiice")
    public void ofiice() throws Exception {
        text = "Офис : " + inputText;

        clearMessage(bot.sendMessage(new SendMessage()
                .setText("Новая заявка\nНапишите суть вопроса")
                .setChatId(chatId)
                .enableHtml(true)
        ));
        step = "Problems";

    }

    @Step(value = "Problems")
    public void Problems() throws Exception {
        text += "\nВопрос : " + inputText;
        task.setText_t(text);
        task.setTenant_id(chatId);
        task.setId_status(1);
        task.setEmployee_id(0);
        id = taskDao.insert(task);
        DataRec rec = taskDao.getTask(id);
        Date daedline = rec.getDate("datadoing");
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm | dd-MM-yyyy");
        String deadlines = dateFormat.format(daedline);
        StringBuilder sb = new StringBuilder();
        sb.append("Новая заявка \n");
        sb.append("Бизнес центр : " + rec.getString("name_c")).append("\n");
        sb.append("Сервис : " + rec.getString("name_p")).append("\n");
        sb.append("Дата : " + deadlines).append("\n");
        sb.append(rec.getString("text_t")).append("\n");
        IKeyboard kb = new IKeyboard();
        kb.next(2);
        kb.add("Подтвердить", Json.set("step", "resepwn").set("id",id).set("r_id",resepshn));
        kb.add("Отменить заявку", Json.set("step", "deleteTask"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText(sb.toString())
                .setChatId(chatId)
                .enableHtml(true)
        ));
    }





    @Step(value = "deleteTask")
    public void deleteTask() throws Exception {
        taskDao.delete(id);
        MainMenuHandle mainMenuHandle = new MainMenuHandle();
        mainMenuHandle.M_menu();
    }


}
