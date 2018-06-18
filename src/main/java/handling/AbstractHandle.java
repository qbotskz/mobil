package handling;

import database.DaoFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import pro.nextbit.telegramconstructor.handle.AbsHandle;

public abstract class AbstractHandle extends AbsHandle {

    protected DaoFactory daoFactory = DaoFactory.getInstance();

    protected boolean hasAccess() throws TelegramApiException {
        if (daoFactory.adminDao().getByChatId(chatId) == null) {
            bot.sendMessage(new SendMessage(chatId, "Нет доступа"));
            redirect("M_menu");
            return false;
        }
        return true;
    }
}
