package components.keyboard;

import com.google.gson.Gson;
import pro.nextbit.telegramconstructor.database.DataRec;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class IKeyboardOld {

    private int[] buttonCounts = null;
    private List<List<InlineKeyboardButton>> inlineList;
    private List<List<List<InlineKeyboardButton>>> inlineTables;

    public void next(int ...buttonCounts){
        this.buttonCounts = buttonCounts;
        setRows();
    }

    public void next(){
        this.buttonCounts = null;
        setRows();
    }

    private void setRows(){
        if (inlineTables == null) {
            inlineTables = new ArrayList<>();
            inlineList = new ArrayList<>();
        }
        else {
            if (inlineList.size() == 0){
                throw new RuntimeException("You have not added any inline buttons");
            }
            else {
                inlineTables.add(inlineList);
                inlineList = new ArrayList<>();
            }
        }
    }

    public InlineKeyboardMarkup generate() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        for (List<List<InlineKeyboardButton>> list:inlineTables){
            keyboard.addAll(list);
        }
        keyboard.addAll(inlineList);
        return new InlineKeyboardMarkup().setKeyboard(keyboard);
    }

    // жанагы библиотека базадан ози таблица создавать етеди
    // енди access_level деген таблицаны создавать ету керек емес
    // еки таблица создавать етеди
    // бириншиси access_level сол баягы таблица
    // сосын access_info деген таблица онда тек chat_id мен id_access_level бар
    // бунын барин автоматом истейди
    // егер бир пользовательди блокировать еткин келсе
    // AccessLevelMap.set(chatId, AccessLevel.WITHOUT_ACCESS) деп корсетесин
    // каз

    public InlineKeyboardButton addButton(String text, DataRec json) {

        if (inlineTables == null){
            throw new RuntimeException("The method 'next' was not called");
        }

        List<InlineKeyboardButton> buttonList;
        InlineKeyboardButton button = new InlineKeyboardButton()
                .setText(text)
                .setCallbackData(new Gson().toJson(json));

        if (buttonCounts != null && buttonCounts.length > 0){

            if (inlineList.size() == 0){

                buttonList = new ArrayList<>();
                buttonList.add(button);
                inlineList.add(buttonList);

            } else {

                int buttonCount = buttonCounts[inlineList.size() - 1];
                buttonList = inlineList.get(inlineList.size() - 1);

                if (buttonList.size() == buttonCount){

                    if (buttonCounts.length == inlineList.size()){
                        throw new RuntimeException("The number of added buttons is more than the specified quantity");
                    } else {

                        buttonList = new ArrayList<>();
                        buttonList.add(button);
                        inlineList.add(buttonList);
                    }

                } else {
                    buttonList.add(button);
                }

            }

        } else {

            buttonList = new ArrayList<>();
            buttonList.add(button);
            inlineList.add(buttonList);

        }

        return button;
    }

}
