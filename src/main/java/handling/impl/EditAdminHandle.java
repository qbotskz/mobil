package handling.impl;

import database.dao.PositionDao;
import database.entity.AdminEntity;
import database.entity.UserEntity;
import handling.AbstractHandle;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import pro.nextbit.telegramconstructor.stepmapping.Step;

import java.util.ArrayList;
import java.util.List;

public class EditAdminHandle extends AbstractHandle {

    private PositionDao positionDao = daoFactory.positionDao();
    private int id;
    private List<AdminEntity> adminEntities;
    private List<UserEntity> userEntities;
    private AdminEntity adminEntity;

    @Step(value = "editAdmin", commandText = "⚙ Смена администраторов")
    public void receptionist() throws Exception {
        if (!hasAccess()) {
            return;
        }
        sendListAdmin();
        step = "editAdmin2";
    }


    @Step(value = "editAdmin2")
    public void receptionist2() throws Exception {
        if (!hasAccess()) {
            return;
        }
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String number = inputText.replaceAll("[^0-9]", ""); //очищаем, оставляя только цифры, на тот случай если случилось опечатка
                if (isEdit(number)) {  // идем в метод для редактирования админов
                    return;
                }
                if (number.length() >= 11) {
                    number = number.substring(1, 10);
                }
                if (number.length() < 4) {
                    sendListAdmin();
                    bot.sendMessage(new SendMessage(chatId, "Нужно указать минимум 4 цифры"));
                    return;
                }
                userEntities = daoFactory.userDao().UserList(number);
                if (userEntities == null || userEntities.size() == 0) {
                    notFindUsers();
                    return;
                }
            }
            if (update.getMessage().getContact() != null) {
                userEntities = new ArrayList<>();
                UserEntity byChatId = null;
                try {
                    byChatId = daoFactory.userDao().getByChatId(update.getMessage().getContact().getUserID());
                    userEntities.add(byChatId);
                } catch (Exception e) {
                    notFindUsers();
                    return;
                }
            }
        }
        sendListAdmin();
    }

    private void checkIsAdmins() {
        try {
            if (userEntities == null || adminEntities == null) {
                return;
            }
            for (AdminEntity entity : adminEntities) {
                for (UserEntity userEntity : userEntities) {
                    if (entity.getChatId() == userEntity.getChat_id()) {
                        //bot.sendMessage(new SendMessage(chatId, "Найден пользователь - " + userEntity.getUser_name() + " но он уже является админитсратором"));
                        userEntities.remove(userEntity);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isEdit(String number) throws TelegramApiException {
        if (inputText.contains("/del")) {
            try {
                if (adminEntities.size() == 1) {  // должен остаться один.
                    return true;
                }
                daoFactory.adminDao().delete(adminEntities.get(Integer.parseInt(number)).getChatId());
            } catch (NumberFormatException e) {
            }
            sendListAdmin();
            return true;
        }
        if (inputText.contains("/add")) {
            try {
                UserEntity userEntity = userEntities.get(Integer.parseInt(number));
                adminEntity = new AdminEntity();
                adminEntity.setChatId((int) userEntity.getChat_id());
                adminEntity.setComment(userEntity.getUser_name() + " - " + userEntity.getPhone());
                try {
                    daoFactory.adminDao().insert(adminEntity);
                } catch (Exception e) {

                }
            } catch (NumberFormatException e) {
            }
            sendListAdmin();
            return true;
        }
        return false;
    }

    private void notFindUsers() throws TelegramApiException {
        sendListAdmin();
        bot.sendMessage(new SendMessage(chatId, "По таким данным, пользователи не найдены."));
    }


    private void sendListAdmin() throws TelegramApiException {
        StringBuilder stringBuilder = new StringBuilder();
        adminEntities = daoFactory.adminDao().AdminListAll();
        checkIsAdmins();
        stringBuilder.append("Для запрета доступа к админке нажмите del, рядом с нужным сотрудником. \n     Список админинстраторов: \n");
        if (adminEntities != null) {
            for (int i = 0; i < adminEntities.size(); i++) {
                stringBuilder.append("❌ /del").append(i).append(" - ").append(adminEntities.get(i).getComment()).append("\n");
            }
        }
        if (userEntities != null && userEntities.size() > 0) {
            stringBuilder.append("Включить доступ к админке - нажмите add, рядом с нужным пользователем. \n     Найдны следующие пользователи: \n");
            for (int i = 0; i < userEntities.size(); i++) {
                stringBuilder.append("✅ /add").append(i).append(" - ").append(userEntities.get(i).getUser_name()).append("\n");
            }
        }
        stringBuilder/*.append("Чтобы добавить нового администратора, нужно найте его в БД зарегистрировнных пользоватлей.\n")*/
                .append("Для поиска отправьте контакт(либо напиште номер телефона или часть его - не менее 4 цифр) пользователя, зарегистрированного в этом боте.");
        clearMessage(bot.sendMessage(new SendMessage(chatId, stringBuilder.toString())));
    }
}
