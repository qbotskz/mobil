package handling.impl;

import components.keyboard.KeyboardOld;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import pro.nextbit.telegramconstructor.stepmapping.Step;

public class AdminHandle extends AbstractHandle {

    @Step(value = "admin", commandText = "/admin") //🔘 Главное меню
    public void admin() throws Exception {
        if (!hasAccess()){
            return;
        }
        KeyboardOld keyboard = new KeyboardOld();
        keyboard.next(2, 2, 2, 2);
        keyboard.addButton("\uD83D\uDEE0 Изменить сообщения");
        keyboard.addButton("\uD83D\uDCCC Смена исполнителя");
        keyboard.addButton("\uD83D\uDD16 Анкеты");
        keyboard.addButton("\uD83C\uDFE2 Конференц-залы");
        keyboard.addButton("⚙ Смена ресепшн");
        keyboard.addButton("⚙ Смена администраторов");
        keyboard.addButton("\uD83D\uDCC9 Отчеты");
        keyboard.addButton("\uD83D\uDD18 Главное меню");
        bot.sendMessage(new SendMessage()
                .setReplyMarkup(keyboard.generate())
                .setText("Главное меню")
                .setChatId(chatId)
        );
    }
}
