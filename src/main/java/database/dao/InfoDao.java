package database.dao;

import pro.nextbit.telegramconstructor.database.DataRec;

public interface InfoDao {

    DataRec getInfo();

    void updateText( String text);

    void updatePhoto(String foto);

    void updateNewsText(String text);

    public void updateNewsPhoto(String foto);

    DataRec getNews();

    DataRec getInfoHoll(int id);

    void updateHollPhoto(String foto, int id);
}
