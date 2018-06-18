package database.dao;

import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.database.DataTable;

public interface PositionDao {

    long getResepshen(int id_centres) ;

    long getResepshenClining ();

    long getResepshnToId(int id);

    DataTable getService();

    DataTable getReceptionist();

    void updateResepwn (long user , int id);

    DataTable getServiceMember(int id);

    int insertService(long user_id, int id_position);

    void deleteServiceMember(int id);
}
