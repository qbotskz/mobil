package database.entity;

import java.sql.Timestamp;

public class TaskEntity {

    private int id;
    private String text_t;
    private long tenant_id;
    private Timestamp dataadd;
    private Timestamp datadoing;
    private int id_status;
    private String comment_t;
    private String clarification;
    private int id_centres;
    private int id_position;
    private int id_category;
    private long employee_id;

    public int getId_centres() {
        return id_centres;
    }

    public void setId_centres(int id_centres) {
        this.id_centres = id_centres;
    }

    public long getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(long employee_id) {
        this.employee_id = employee_id;
    }

    public int getId_position() {
        return id_position;
    }

    public void setId_position(int id_position) {
        this.id_position = id_position;
    }

    public int getId_category() {
        return id_category;
    }

    public void setId_category(int id_category) {
        this.id_category = id_category;
    }

    public int getId_centre() {
        return id_centres;
    }

    public void setId_centre(int id_centre) {
        this.id_centres = id_centre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText_t() {
        return text_t;
    }

    public void setText_t(String text_t) {
        this.text_t = text_t;
    }

    public long getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(long tenant_id) {
        this.tenant_id = tenant_id;
    }

    public Timestamp getDataadd() {
        return dataadd;
    }

    public void setDataadd(Timestamp dataadd) {
        this.dataadd = dataadd;
    }

    public Timestamp getDatadoing() {
        return datadoing;
    }

    public void setDatadoing(Timestamp datadoing) {
        this.datadoing = datadoing;
    }

    public int getId_status() {
        return id_status;
    }

    public void setId_status(int id_status) {
        this.id_status = id_status;
    }

    public String getComment_t() {
        return comment_t;
    }

    public void setComment_t(String comment_t) {
        this.comment_t = comment_t;
    }

    public String getClarification() {
        return clarification;
    }

    public void setClarification(String clarification) {
        this.clarification = clarification;
    }
}
