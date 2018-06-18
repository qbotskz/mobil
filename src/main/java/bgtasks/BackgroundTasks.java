package bgtasks;

import database.DaoFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class BackgroundTasks {

    private static TelegramLongPollingBot bot;
    private DaoFactory daoFactory = DaoFactory.getInstance();

    public void setBot(TelegramLongPollingBot inputBot) {
        bot = inputBot;
    }


    // описание cron (секунд минут час день месяц год)
    // если * тогда повторяется
    @Scheduled(cron = "0 0 12 * * ?")
    public void createData() {

        try {
            // Здесь должен быть код
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}



