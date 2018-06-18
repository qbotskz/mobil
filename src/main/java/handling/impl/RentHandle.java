package handling.impl;

import components.keyboard.KeyboardOld;
import database.dao.InfoDao;
import database.dao.PositionDao;
import database.dao.RentDao;
import database.dao.UserDao;
import database.entity.UserEntity;
import handling.AbstractHandle;
import org.joda.time.DateTime;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.TimePicker;
import pro.nextbit.telegramconstructor.components.datepicker.FullDatePicker;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RentHandle extends AbstractHandle {
    private StringBuilder sb;
    private StringBuilder sb_hol;
    private InfoDao infoDao = daoFactory.infoDao();

    private RentDao rentDao = daoFactory.rentDao();
    private PositionDao positionDao = daoFactory.positionDao();
    private UserDao userDao = daoFactory.userDao();
    private Timestamp datebegin;
    private DateTime date;

    @Step(value = "rent", commandText = "\uD83C\uDFE2 Отдел аренды") //🔘 Главное меню
    public void rent() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("\uD83D\uDD8C Аренда офиса", Json.set("step", "rentOffice"));
        kb.add("\uD83D\uDD8A Аренда конференц-зала ", Json.set("step", "rent_holl"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Меню «Отдел аренды»")
                .setChatId(chatId)
        ));

    }


    @Step(value = "rentOffice")
    public void rentOffice() throws Exception {
        sb = new StringBuilder();
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("Нұрсаулет-1", Json.set("step", "rentOffice1").set("bc", "Нұрсаулет-1"));
        kb.add("Нұрсаулет-2", Json.set("step", "rentOffice1").set("bc", "Нұрсаулет-2"));
        kb.add("Без разницы", Json.set("step", "rentOffice1").set("bc", "Без разницы"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("«Выбор бизнес центра»")
                .setReplyMarkup(kb.generate())
                .setChatId(chatId)
                .enableHtml(true)));
    }


    @Step(value = "rentOffice1")
    public void rentOffice1() throws Exception {
        sb.append("Бизнес центр: ").append(queryData.getString("bc")).append("\n");
        IKeyboard kb = new IKeyboard();
        kb.next(2, 2, 1);
        kb.add("50-100 кв.м", Json.set("step", "rentOffice2").set("bc", "50-100 кв.м"));
        kb.add("100-200 кв.м", Json.set("step", "rentOffice2").set("bc", "100-200 кв.м"));
        kb.add("200-400 кв.м", Json.set("step", "rentOffice2").set("bc", "200-400 кв.м"));
        kb.add("400-800 кв.м", Json.set("step", "rentOffice2").set("bc", "400-800 кв.м"));
        kb.add("800-1000 кв.м", Json.set("step", "rentOffice2").set("bc", "800-1000 кв.м"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Выберите желаемую площадь кв.м")
                .setChatId(chatId)
                .setReplyMarkup(kb.generate())
                .enableHtml(true)));
    }

    @Step(value = "rentOffice2")
    public void rentOffice2() throws Exception {
        sb.append("Выбрана площадь:  ").append(queryData.getString("bc")).append("\n");
        IKeyboard kb = new IKeyboard();
        kb.next(2, 2, 1);
        kb.add("без комментария", Json.set("step", "rentOffice3").set("bc", "без комментария"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Добавьте комментарий или нажмите без комментария")
                .setChatId(chatId)
                .setReplyMarkup(kb.generate())
                .enableHtml(true)));
        step = "rentOffice3";
    }


    @Step(value = "rentOffice3")
    public void rentOffice3() throws Exception {
        if (queryData.containsKey("bc")) {
            sb.append("Комментарий :  ").append(queryData.getString("bc")).append("\n");
        } else {
            sb.append("Комментарий :  ").append(inputText).append("\n");
        }
        IKeyboard kb = new IKeyboard();
        kb.next();
        kb.add("Да", Json.set("step", "rentOffice4").set("bc", 1));
        kb.add("Нет", Json.set("step", "rentOffice4").set("bc", 2));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText(sb.toString() + "Заявка готова, отправить?")
                .setChatId(chatId)
                .setReplyMarkup(kb.generate())
                .enableHtml(true)));
    }


    @Step(value = "rentOffice4")
    public void rentOffice4() throws Exception {
        long ressep_of = positionDao.getResepshnToId(9);
        if (queryData.getInt("bc") == 1) {
            UserEntity user = userDao.getByChatId(chatId);
            sb.append("От : ").append(user.getUser_name()).append("\n");
            sb.append("Тел : ").append(user.getPhone()).append("\n");
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
            rentDao.insert(sb.toString(), chatId, new Timestamp(dateNows.toDate().getTime()));
            clearMessageOnClick(bot.sendMessage(new SendMessage()
                    .setText("Новая заявка на аренду офисов\n" + sb)
                    .setChatId(ressep_of)
                    .enableHtml(true)));
        } else {
            clearMessageOnClick(bot.sendMessage(new SendMessage()
                    .setText("Вы отменили заявку!\n" + sb)
                    .setChatId(chatId)
                    .enableHtml(true)));
        }

    }

    //-------------------------------------------Аренда конференц зала---------------------------

    @Step(value = "rent_holl")
    public void rent_holl() throws Exception {
        sb_hol = new StringBuilder();
        clearMessage(bot.sendMessage(new SendMessage()
                .setText("Название Вашей компании?")
                .setChatId(chatId)
                .enableHtml(true)));
        step = "rent_holl01";

    }

    @Step(value = "rent_holl01")
    public void rent_holl01() throws Exception {
        sb_hol.append("Компания : ").append(inputText).append("\n");
        clearMessage(bot.sendMessage(new SendMessage()
                .setText("Ваша должность?")
                .setChatId(chatId)
                .enableHtml(true)));
        step = "rent_holl02";
    }

    @Step(value = "rent_holl02")
    public void rent_holl02() throws Exception {
        sb_hol.append("Должность : ").append(inputText).append("\n");
        clearMessage(bot.sendMessage(new SendMessage()
                .setText("Напишите свои Фамилия Имя")
                .setChatId(chatId)
                .enableHtml(true)));
        step = "rent_holl03";
    }

    @Step(value = "rent_holl03")
    public void rent_holl03() throws Exception {
        sb_hol.append("Ф.И  : ").append(inputText).append("\n");
        redirect("rent_holl2");
    }


    @Step(value = "rent_holl2")
    public void rent_holl2() throws Exception {

        IKeyboard kb = new IKeyboard();
        kb.next(2, 2);
        kb.add("Нұрсаулет-1 зал 1", Json.set("step", "rent_holl3").set("bc", 1).set("1", "Нұрсаулет-1 зал 1"));
        kb.add("Нұрсаулет-2 зал 1", Json.set("step", "rent_holl3").set("bc", 2).set("1", "Нұрсаулет-2 зал 1"));
        kb.add("Нұрсаулет-2 зал 2", Json.set("step", "rent_holl3").set("bc", 3).set("1", "Нұрсаулет-2 зал 2"));
        kb.add("Нұрсаулет-2 зал 3", Json.set("step", "rent_holl3").set("bc", 4).set("1", "Нұрсаулет-2 зал 3"));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Выберите зал для просмотра фото")
                .setChatId(chatId)
                .setReplyMarkup(kb.generate())
                .enableHtml(true)));
        step = "rentOffice3";
    }

    @Step(value = "rent_holl3")
    public void rent_holl3() throws Exception {
        DataRec rec = infoDao.getInfoHoll(queryData.getInt("bc"));

        IKeyboard kb = new IKeyboard();
        kb.next(2);
        kb.add("Подтвердить", Json.set("step", "rent_holl4").set("bc", queryData.getString("1")));
        kb.add("Обратно", Json.set("step", "rent_holl2"));
        clearMessageOnClick(bot.sendPhoto(new SendPhoto()
                .setChatId(chatId)
                .setPhoto(rec.getString("photo"))
                .setCaption(rec.getString("text_ih"))
                .setReplyMarkup(kb.generate())));
        step = "rentOffice3";
    }

    @Step(value = "rent_holl4")
    public void rent_holl4() throws Exception {
        sb_hol.append("Выбран зал : " + queryData.getString("bc") + "\n");
        IKeyboard kb = new IKeyboard();
        kb.next(2);
        kb.add("C оборудованием", Json.set("step", "rent_holl5").set("bc", "C оборудованием"));
        kb.add("Без оборудования", Json.set("step", "rent_holl5").set("bc", "Без оборудования"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Выберите зал для просмотра фото")
                .setChatId(chatId)
                .setReplyMarkup(kb.generate())
                .enableHtml(true)));
        step = "rentOffice3";
    }

    @Step(value = "rent_holl5")
    public void rent_holl5() throws Exception {
        sb_hol.append("Вариант аренды : " + queryData.getString("bc") + "\n");
        redirect("rent_holl6");
    }

    @Step(value = "rent_holl6")
    public void rent_holl6() throws Exception {
        FullDatePicker datePicker = new FullDatePicker(queryData, step);
        date = datePicker.getDate(bot, "Выберите дату ", message);

        DateTime times = new DateTime();
        TimePicker pickers = new TimePicker(queryData, step, times);
        times = pickers.getDateTime(bot, "Укажите желаемое время начала аренды:", message);


        Calendar cals = Calendar.getInstance();
        cals.set(Calendar.YEAR, date.getYear());
        cals.set(Calendar.MONTH, date.getMonthOfYear());
        cals.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        cals.set(Calendar.HOUR_OF_DAY, times.getHourOfDay());
        cals.set(Calendar.MINUTE, times.getMinuteOfHour());
        cals.set(Calendar.SECOND, 0);
        cals.set(Calendar.MILLISECOND, 0);

        DateTime deadlines = new DateTime(cals).minusMonths(1);
        datebegin = new Timestamp(deadlines.toDate().getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM | HH:mm");
        String dates = dateFormat.format(datebegin);

        sb_hol.append("Время: ").append(dates);
        redirect("rent_holl7");
    }


    @Step(value = "rent_holl7")
    public void rent_holl7() throws Exception {
        DateTime times = new DateTime();
        TimePicker pickers = new TimePicker(queryData, step, times);
        times = pickers.getDateTime(bot, "Укажите до скольки продлится аренда", message);

        Calendar cals = Calendar.getInstance();
        cals.set(Calendar.YEAR, date.getYear());
        cals.set(Calendar.MONTH, date.getMonthOfYear());
        cals.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        cals.set(Calendar.HOUR_OF_DAY, times.getHourOfDay());
        cals.set(Calendar.MINUTE, times.getMinuteOfHour());
        cals.set(Calendar.SECOND, 0);
        cals.set(Calendar.MILLISECOND, 0);
        DateTime deadlines = new DateTime(cals).minusMonths(1);
        Timestamp t = new Timestamp(deadlines.toDate().getTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String dates = dateFormat.format(t);

        sb_hol.append(" - " + dates + "\n");
       int id =  rentDao.insertHoll(sb_hol.toString(), chatId, datebegin, t);

        IKeyboard kb = new IKeyboard();
        kb.next(2);
        kb.add("Да", Json.set("step", "rent_holl8").set("bc", 1));
        kb.add("Нет", Json.set("step", "rent_holl8").set("bc", 2).set("id", id));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Новая заявка \nНа аренду конференц-зала\n" + sb_hol)
                .setChatId(chatId)
                .setReplyMarkup(kb.generate())
                .enableHtml(true)));
    }


    @Step(value = "rent_holl8")
    public void rent_holl8() throws Exception {
        long ressep_holl = positionDao.getResepshnToId(8);

        if (queryData.getInt("bc") == 1) {
            UserEntity user = userDao.getByChatId(chatId);
            bot.sendMessage(new SendMessage()
                    .setText("Ваша заявка успешно создана и отправлена к администрации")
                    .setChatId(chatId)
                    .enableHtml(true));

            bot.sendMessage(new SendMessage()
                    .setText("Новая заявка \nНа аренду конференц-зала\n" + sb_hol + "Тел : " + user.getPhone())
                    .setChatId(ressep_holl)
                    .enableHtml(true));

        } else {
            bot.sendMessage(new SendMessage()
                    .setText("Заявка отменена")
                    .setChatId(chatId)
                    .enableHtml(true));
            rentDao.deleteHoll(queryData.getInt("id"));

        }
    }
}
