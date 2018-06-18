package database.dao;

import pro.nextbit.telegramconstructor.database.DataTable;

import java.sql.Timestamp;


public interface QuestionnaireDao {
    DataTable getList();

    int insertQuestion(String text);

    int insertVariant(String text, int id_questions);

    DataTable getVariant(int id_questions);

    void deleteVar(int id_questions);

    void deleteQues(int id);

    int countVariant(int id_variant, Timestamp datebegin, Timestamp deadline);

    int countQuestion(int id_variant, Timestamp datebegin, Timestamp deadline);

    DataTable chek (int id_questions);

    DataTable chek(int id_questions, long chatId);

    int insertResult(long user_id, int id_questions, int id_variant, Timestamp date);


    void deleteAnswers(int idQuestion);
}
