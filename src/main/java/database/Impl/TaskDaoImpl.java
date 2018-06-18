package database.Impl;

import database.dao.TaskDao;
import database.entity.TaskEntity;
import pro.nextbit.telegramconstructor.database.DataBaseUtils;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.database.DataTable;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TaskDaoImpl implements TaskDao {

    private DataBaseUtils utils;

    public TaskDaoImpl(DataSource source) {
        this.utils = new DataBaseUtils(source);
    }


    @Override
    public int insert(TaskEntity task) {
        return (int) utils.updateForKeyId(
                "INSERT INTO task (text_t, tenant_id, dataadd, datadoing," +
                        " id_status, comment_t, clarification,id_centres,id_category,id_position, employee_id) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                task.getText_t(), task.getTenant_id(), task.getDataadd(), task.getDatadoing(),
                task.getId_status(), task.getComment_t(), task.getClarification(), task.getId_centre(),
                task.getId_category(), task.getId_position(), task.getEmployee_id()
        );
    }

    @Override
    public DataTable getMytaskList(long tenant_id) {
        return utils.query("SELECT * FROM task WHERE tenant_id = ? " +
                "AND  (id_status = 1 OR id_status = 2 OR id_status = 3 " +
                "OR  id_status = 4)", tenant_id);
    }

    @Override
    public DataTable getMytaskEmpl(long employee_id) {
        return utils.query("SELECT * FROM task WHERE employee_id = ? " +
                "AND (id_status = 2 OR id_status = 3)", employee_id);
    }

    @Override
    public DataTable getMytasRes() {
        return utils.query("SELECT * FROM task WHERE " +
                "id_status = 1");
    }

    @Override
    public DataRec getTask(int id) {
        return utils.queryDataRec("SELECT task.id,* FROM task INNER JOIN users ON task.tenant_id = users.chat_id\n" +
                "LEFT JOIN  centres ON task.id_centres = centres.id\n" +
                "LEFT JOIN  position ON task.id_position = position.id\n" +
                "LEFT JOIN  status ON task.id_status = status.id WHERE task.id = ? ", id

        );
    }

    @Override
    public DataRec getTaskOnly(int id) {
        return utils.queryDataRec("SELECT * FROM task WHERE id = ?", id);
    }


    @Override
    public DataTable getPosition(int id) {
        return utils.query("SELECT * FROM task LEFT JOIN service ON task.id_position = service.id_position" +
                " INNER JOIN users\n" +
                "  ON service.user_id=users.chat_id WHERE task.id = ?", id);
    }


    @Override
    public void delete(int id) {
        utils.update(
                "DELETE FROM task WHERE id=? ", id
        );
    }


    @Override
    public void updatEmployee(long employee, int id) {
        utils.update(
                "UPDATE task SET employee_id = ? WHERE id = ?",
                employee, id
        );
    }

    @Override
    public void updatStatus(int id_status, int id) {
        utils.update(
                "UPDATE task SET id_status = ? WHERE id = ?",
                id_status, id
        );
    }


    @Override
    public void updatClarification(String clarification, int id) {
        utils.update(
                " UPDATE task SET clarification = ? WHERE id = ?",
                clarification, id
        );
    }


    @Override
    public DataTable getTaskStat(Timestamp datebegin, Timestamp deadline) {
        return utils.query("SELECT task.id AS id_task,* FROM task INNER JOIN users ON task.tenant_id = users.chat_id\n" +
                "LEFT JOIN  centres ON task.id_centres = centres.id " +
                "LEFT JOIN  position ON task.id_position = position.id " +
                "LEFT JOIN  status ON task.id_status = status.id WHERE task.dataadd BETWEEN " +
                "? AND ? ORDER BY id_task", datebegin, deadline

        );
    }


    @Override
    public int countDone(Timestamp datebegin, Timestamp deadline) {
        return utils.queryDataRec("SELECT count(id) AS total FROM task WHERE (id_status = 4 OR " +
                "id_status = 7) AND dataadd BETWEEN ? AND ?", datebegin, deadline
        ).getInt("total");
    }

    @Override
    public int countNotDone(Timestamp datebegin, Timestamp deadline) {
        return utils.queryDataRec("SELECT count(id) AS total FROM task WHERE id_status = 5" +
                " AND dataadd BETWEEN ? AND ? ", datebegin, deadline
        ).getInt("total");
    }


    @Override
    public int countDoing(Timestamp datebegin, Timestamp deadline) {
        return utils.queryDataRec("SELECT count(id) AS total FROM task WHERE (id_status = 1 OR " +
                "id_status = 2 OR id_status = 3 OR  id_status = 6) AND dataadd BETWEEN ? AND ?", datebegin, deadline
        ).getInt("total");
    }


    @Override
    public int countDoneAll() {
        return utils.queryDataRec("SELECT count(id) AS total FROM task WHERE (id_status = 4 OR " +
                "id_status = 7) "
        ).getInt("total");
    }

    @Override
    public int countNotDoneAll() {
        return utils.queryDataRec("SELECT count(id) AS total FROM task WHERE id_status = 5").getInt("total");
    }


    @Override
    public int countDoingAll() {
        return utils.queryDataRec("SELECT count(id) AS total FROM task WHERE id_status = 1 OR " +
                "id_status = 2 OR id_status = 3 OR  id_status = 6").getInt("total");
    }

    private TaskEntity mapper(ResultSet rs, int index) throws SQLException {

        TaskEntity task = new TaskEntity();
        task.setId(rs.getInt("id"));
        task.setText_t(rs.getString("text_t"));
        task.setTenant_id(rs.getLong("tenant_id"));
        task.setDataadd(rs.getTimestamp("dataadd"));
        task.setDatadoing(rs.getTimestamp("datadoing"));
        task.setId_status(rs.getInt("id_status"));
        task.setComment_t(rs.getString("comment_t"));
        task.setClarification(rs.getString("clarification"));
        task.setId_centre(rs.getInt("id_centre"));
        task.setId_position(rs.getInt("id_position"));
        task.setId_category(rs.getInt("id_category"));
        task.setEmployee_id(rs.getLong("employee_id"));
        return task;
    }
}
