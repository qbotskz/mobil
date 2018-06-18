package database.dao;

import database.entity.TaskEntity;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.database.DataTable;

import java.sql.Timestamp;
import java.util.List;

public interface TaskDao {

    int insert(TaskEntity task);

    DataRec getTask(int id);

    void delete(int id);

    DataTable getPosition(int id);

    DataRec getTaskOnly(int id);

    void updatEmployee  (long employee, int id);

    void updatStatus  (int id_status, int id);

     void updatClarification  (String clarification, int id);

    DataTable getMytaskList(long tenant_id);

    DataTable getMytaskEmpl(long employee_id);

    DataTable getMytasRes();

    DataTable getTaskStat(Timestamp datebegin, Timestamp deadline);

    int countDone( Timestamp datebegin, Timestamp deadline);

    int countNotDone( Timestamp datebegin, Timestamp deadline);

    int countDoing(Timestamp datebegin, Timestamp deadline);

    int countDoneAll();

    int countNotDoneAll();

    int countDoingAll();

}
