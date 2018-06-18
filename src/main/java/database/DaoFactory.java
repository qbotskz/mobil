package database;


import database.Impl.*;
import database.dao.*;

import javax.sql.DataSource;

public class DaoFactory {

    private DaoFactory(){}

    private static DataSource source;
    private static DaoFactory ourInstance = new DaoFactory();

    public void setDataSource(DataSource dataSource) {
        source = dataSource;
    }

    public static DaoFactory getInstance() {
        return ourInstance;
    }

    public UserDao userDao(){return new UserDaoImpl(source);}

    public  TaskDao taskDao(){return new TaskDaoImpl(source);}

    public PositionDao positionDao(){return new PositionDaoImpl(source);}

    public InfoDao infoDao(){return new InfoDaoImpl(source);}

    public  RentDao rentDao(){return new RentDaoImpl(source);}

    public QuestionnaireDao questionnaireDao(){return new QuestionnaireDaoImpl(source);}

    public AdminDao adminDao (){
        return new AdminDaoImpl(source);
    }




}
