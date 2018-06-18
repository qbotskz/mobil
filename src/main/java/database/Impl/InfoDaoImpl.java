package database.Impl;

import database.dao.InfoDao;
import pro.nextbit.telegramconstructor.database.DataBaseUtils;
import pro.nextbit.telegramconstructor.database.DataRec;

import javax.sql.DataSource;

public class InfoDaoImpl implements InfoDao {

    private DataBaseUtils utils;

    public InfoDaoImpl(DataSource source) {
        this.utils = new DataBaseUtils(source);
    }


    @Override
    public DataRec getInfo() {
        return utils.queryDataRec("SELECT * FROM info ");
    }


    @Override
    public void updateText(String text) {
        utils.update(
                "UPDATE info SET text_in = ? WHERE id = 1", text);
    }

    public void updatePhoto(String foto) {
        utils.update(
                "UPDATE info SET foto_in = ? WHERE id = 1", foto);
    }

    @Override
    public void updateNewsText(String text) {
        utils.update(
                "UPDATE news SET text_n = ? WHERE id = 1", text);
    }

    public void updateNewsPhoto(String foto) {
        utils.update(
                "UPDATE news SET foto_n = ? WHERE id = 1", foto);
    }

    @Override
    public DataRec getNews() {
        return utils.queryDataRec("SELECT * FROM news ");
    }


    @Override
    public DataRec getInfoHoll(int id) {
        return utils.queryDataRec("SELECT * FROM info_holl WHERE id = ?", id);
    }

    public void updateHollPhoto(String foto, int id) {
        utils.update(
                "UPDATE info_holl SET photo = ? WHERE id = ?", foto, id);
    }

}
