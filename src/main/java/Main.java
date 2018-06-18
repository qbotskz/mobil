import bgtasks.BackgroundTasks;
import bgtasks.BackgroundTaskConfig;
import database.DaoFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import pro.nextbit.telegramconstructor.accesslevel.AccessLevelMap;
import pro.nextbit.telegramconstructor.database.AppProperties;
import pro.nextbit.telegramconstructor.handle.Bot;
import pro.nextbit.telegramconstructor.stepmapping.StepMapping;

import javax.sql.DataSource;

public class Main {

    @SuppressWarnings({"unused", "resource"})
    public static void main(String[] args) {

        Logger log = LogManager.getLogger("Main");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {

            log.info("Running telegrams bot...");
            telegramBotsApi.registerBot(initBot());
            log.info("Telegrams bot is running");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private static DataSource getDataSource() {
        DriverManagerDataSource driver = new DriverManagerDataSource();
        driver.setDriverClassName(AppProperties.getProp("jdbc.driverClassName"));
        driver.setUrl(AppProperties.getProp("jdbc.url"));
        driver.setUsername(AppProperties.getProp("jdbc.username"));
        driver.setPassword(AppProperties.getProp("jdbc.password"));
        return driver;
    }

    private static Bot initBot() throws Exception {

        new AppProperties().init("app.properties");
        DaoFactory.getInstance().setDataSource(getDataSource());
        new AnnotationConfigApplicationContext(BackgroundTaskConfig.class);
        new AccessLevelMap().init(getDataSource());
        new StepMapping().initializeMapping("handling.impl");
        ApiContextInitializer.init();

        Bot bot = new Bot();
        bot.setBotUserName(AppProperties.getProp("botUserName"));
        bot.setBotToken(AppProperties.getProp("botToken"));

        new BackgroundTasks().setBot(bot);
        return bot;
    }
}
