package database.Impl;

import database.dao.QuestionnaireDao;
import pro.nextbit.telegramconstructor.database.DataBaseUtils;
import pro.nextbit.telegramconstructor.database.DataTable;

import javax.sql.DataSource;
import java.sql.Timestamp;

public class QuestionnaireDaoImpl implements QuestionnaireDao {
    private DataBaseUtils utils;

    public QuestionnaireDaoImpl(DataSource source) {
        this.utils = new DataBaseUtils(source);
    }


    @Override
    public DataTable getList() {
        return utils.query("SELECT * FROM questions ");
    }

    @Override
    public int insertQuestion(String text) {
        return (int) utils.updateForKeyId(
                "INSERT INTO questions (text_q) " +
                        "VALUES (?)", text);
    }

    @Override
    public int insertVariant(String text, int id_questions) {
        return (int) utils.updateForKeyId(
                "INSERT INTO variant (text_v, id_questions) " +
                        "VALUES (?,?)", text, id_questions);
    }

    @Override
    public DataTable getVariant(int id_questions) {
        return utils.query("SELECT * FROM variant WHERE id_questions = ?", id_questions);
    }

    @Override
    public void deleteVar(int id_questions) {
        utils.update(
                "DELETE FROM variant WHERE id_questions=? ", id_questions);
    }

    @Override
    public void deleteQues(int id) {
        utils.update(
                "DELETE FROM questions WHERE id=? ", id);
    }


    @Override
    public int countVariant(int id_variant, Timestamp datebegin, Timestamp deadline) {
        return utils.queryDataRec("SELECT count(id) AS total FROM questionslist WHERE id_variant = ?" +
                " AND date BETWEEN ? AND ?", id_variant, datebegin, deadline
        ).getInt("total");
    }

    @Override
    public int countQuestion(int id_variant, Timestamp datebegin, Timestamp deadline) {
        return utils.queryDataRec("SELECT count(id) AS total FROM questionslist WHERE id_questions = ?" +
                " AND date BETWEEN ? AND ?", id_variant, datebegin, deadline
        ).getInt("total");
    }

    @Override
    public DataTable chek(int id_questions) {
        return utils.query("SELECT * FROM questionslist WHERE id_questions = ? ", id_questions);
    }

    @Override
    public DataTable chek(int id_questions, long chatId) {
        return utils.query("SELECT * FROM questionslist WHERE id_questions = ? AND user_id = ?", id_questions, chatId);
    }

    @Override
    public int insertResult(long user_id, int id_questions, int id_variant, Timestamp date) {
        return (int) utils.updateForKeyId(
                "INSERT INTO questionslist (user_id, id_questions, id_variant, date) " +
                        "VALUES (?,?,?,?)", user_id, id_questions, id_variant, date);
    }

    @Override
    public void deleteAnswers(int idQuestion) {
        utils.update("DELETE FROM questionslist WHERE id_questions=? ", idQuestion);
    }
}
