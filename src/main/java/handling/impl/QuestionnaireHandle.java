package handling.impl;

import database.dao.QuestionnaireDao;
import handling.AbstractHandle;
import org.joda.time.DateTime;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import pro.nextbit.telegramconstructor.Json;
import pro.nextbit.telegramconstructor.components.keyboard.IKeyboard;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.stepmapping.Step;

import java.sql.Timestamp;
import java.util.Calendar;

public class QuestionnaireHandle extends AbstractHandle {

    private QuestionnaireDao questionnaireDao = daoFactory.questionnaireDao();
    private int id;

    //--------------------Анкета в ранеле админа---------------------------------
    @Step(value = "questionnaire", commandText = "\uD83D\uDD16 Анкеты")
    public void questionnaire() throws Exception {
        if (!hasAccess()) {
            return;
        }
        IKeyboard kb = new IKeyboard();
        kb.next();
        for (DataRec rec : questionnaireDao.getList()) {
            kb.add(rec.getString("text_q"), Json.set("step", "quetsionView").set("id", rec.getInt("id")));
        }
        kb.add("Добавить", Json.set("step", "addQuestionnaire"));
        kb.add("Удалить", Json.set("step", "quetsionDelete"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Анкеты")
                .setChatId(chatId)));
    }


    @Step(value = "addQuestionnaire")
    public void addQuestionnaire() throws Exception {
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Выберите текст анкеты (вопрос)")
                .setChatId(chatId)));
        step = "addQuestionnaire01";
    }


    @Step(value = "addQuestionnaire01")
    public void addQuestionnaire01() throws Exception {
        id = questionnaireDao.insertQuestion(inputText);
        redirect("addQuestionnaire2");
    }

    @Step(value = "addQuestionnaire2")
    public void addQuestionnaire2() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Введите варианты ответа")
                .setChatId(chatId)));
        step = "addQuestionnaire3";
    }

    @Step(value = "addQuestionnaire3")
    public void addQuestionnaire3() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        questionnaireDao.insertVariant(inputText, id);
        for (DataRec rec : questionnaireDao.getVariant(id)) {
            kb.add(rec.getString("text_v"), Json.set("step", "addQuestionnaire2").set("id", rec.getInt("id")));
        }
        kb.add("Завершить", Json.set("step", "questionnaire"));
        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Введите варианты ответа")
                .setReplyMarkup(kb.generate())
                .setChatId(chatId)));
    }

    @Step(value = "quetsionDelete")
    public void quetsionDelete() throws Exception {

        IKeyboard kb = new IKeyboard();
        kb.next();
        for (DataRec rec : questionnaireDao.getList()) {
            kb.add(rec.getString("text_q"), Json.set("step", "quetsionDelete2").set("id", rec.getInt("id")));
        }

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setReplyMarkup(kb.generate())
                .setText("Выберите вопрос для удаления")
                .setChatId(chatId)));

    }

    @Step(value = "quetsionDelete2")
    public void quetsionDelete2() throws Exception {
        int id =queryData.getInt("id");
        questionnaireDao.deleteVar(id);
        questionnaireDao.deleteQues(id);
        questionnaireDao.deleteAnswers(id);
        bot.sendMessage(new SendMessage()
                .setText("Данные успешно удалены")
                .setChatId(chatId));
        redirect("questionnaire");
    }


    @Step(value = "quetsionView")
    public void quetsionView() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        for (DataRec rec : questionnaireDao.getVariant(queryData.getInt("id"))) {
            kb.add(rec.getString("text_v"), Json.set("step", "").set("id", rec.getInt("id")));
        }
        kb.add("Назад", Json.set("step", "questionnaire"));

        clearMessageOnClick(bot.sendMessage(new SendMessage()
                .setText("Варианты ответов")
                .setReplyMarkup(kb.generate())
                .setChatId(chatId)));
    }


    //--------------------Анкета в главном меню---------------------------------


    @Step(value = "questUser", commandText = "\uD83D\uDDC2 Анкеты")
    public void questUser() throws Exception {
        IKeyboard kb = new IKeyboard();
        kb.next();
        boolean cycle = false;

        for (DataRec rec : questionnaireDao.getList()) {
            if (questionnaireDao.chek(rec.getInt("id"), chatId).size() == 0) {
                if (!cycle) {
                    for (DataRec var : questionnaireDao.getVariant(rec.getInt("id"))) {
                        kb.add(var.getString("text_v"), Json.set("step", "questUser2")
                                .set("id_q", rec.getInt("id")).set("id_v", var.getInt("id")));
                    }

                    try {
                        clearMessageOnClick(bot.sendMessage(new SendMessage()
                                .setText(rec.getString("text_q"))
                                .setReplyMarkup(kb.generate())
                                .setChatId(chatId)));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    cycle = true;
                }
            }
        }

        if (!cycle) {
            clearMessageOnClick(bot.sendMessage(new SendMessage()
                    .setText("Извините, Вы участвовали во всех доступных опросах")
                    .setChatId(chatId)));
        }
    }

    @Step(value = "questUser2")
    public void questUser2() throws Exception {
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
        questionnaireDao.insertResult(chatId, queryData.getInt("id_q"), queryData.getInt("id_v"),
                new Timestamp(dateNows.toDate().getTime()));

        bot.sendMessage(new SendMessage()
                .setText("Ваш ответ записан\n" +
                        "Спасибо за уделенное время!")
                .setChatId(chatId));
        redirect("questUser");
    }

}
