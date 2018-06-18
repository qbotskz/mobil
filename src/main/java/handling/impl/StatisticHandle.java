package handling.impl;

import database.dao.QuestionnaireDao;
import database.dao.RentDao;
import database.dao.TaskDao;
import database.dao.UserDao;
import handling.AbstractHandle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import pro.nextbit.telegramconstructor.components.datepicker.FullDatePicker;
import pro.nextbit.telegramconstructor.database.DataRec;
import pro.nextbit.telegramconstructor.database.DataTable;
import pro.nextbit.telegramconstructor.stepmapping.Step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatisticHandle extends AbstractHandle {
    private TaskDao taskDao = daoFactory.taskDao();
    private UserDao userDao = daoFactory.userDao();
    private RentDao rentDao = daoFactory.rentDao();
    private QuestionnaireDao questionnaireDao = daoFactory.questionnaireDao();
    private DateTime databegin;
    private DateTime deadline;

    @Step(value = "statistic", commandText = "\uD83D\uDCC9 Отчеты")
    public void statistic() throws Exception {
        if (!hasAccess()) {
            return;
        }
        FullDatePicker datePicker = new FullDatePicker(queryData, step);
        databegin = datePicker.getDate(bot, "Выберите дату ", message);
        redirect("statistic2");
    }


    @Step("statistic2") // Главное меню
    public void statistic2() throws Exception {
        FullDatePicker datePicker = new FullDatePicker(queryData, step);
        deadline = datePicker.getDate(bot, "Выберите дату ", message);
        bot.sendMessage(new SendMessage(chatId, "Отчет подготавливается..."));
        new Thread(() -> {
            try {
                createReport();
            } catch (Exception e) {
                try {
                    bot.sendMessage(new SendMessage(chatId, "Ошибка отправки отчета"));
                    log.error(this.getClass(), e.getCause());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    private void createReport() throws IOException, TelegramApiException {
        DataTable task = taskDao.getTaskStat(new Timestamp(databegin.toDate().getTime()), new Timestamp(deadline.toDate().getTime()));

        int done = taskDao.countDone(new Timestamp(databegin.toDate().getTime()), new Timestamp(deadline.toDate().getTime()));
        int doing = taskDao.countDoing(new Timestamp(databegin.toDate().getTime()), new Timestamp(deadline.toDate().getTime()));
        int notDone = taskDao.countNotDone(new Timestamp(databegin.toDate().getTime()), new Timestamp(deadline.toDate().getTime()));
        int total = done + doing + notDone;


        int doneAll = taskDao.countDoneAll();
        int doingAll = taskDao.countDoingAll();
        int notDoneAll = taskDao.countNotDoneAll();
        int totalAll = doneAll + doingAll + notDoneAll;

        SimpleDateFormat dateFormats = new SimpleDateFormat("dd-MM-yyyy");
        String datebegins = dateFormats.format(databegin.toDate());
        String deadliness = dateFormats.format(deadline.toDate());
        String period = datebegins + " - " + deadliness;

        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheets = wb.createSheet("Статистика");
        Sheet sheet_of = wb.createSheet("Аренда Офисов");
        Sheet Holl = wb.createSheet("Аренда Конференц-залов");
        Sheet questions = wb.createSheet("Анкета");
        Sheet users = wb.createSheet("Пользователи");
        // -------------------------Стиль ячеек-------------------------
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle style = wb.createCellStyle();


        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);
        // style.setFillForegroundColor(new XSSFColor(Color.YELLOW).getIndexed());


        BorderStyle tittle = BorderStyle.THICK;
        XSSFCellStyle styleTitle = wb.createCellStyle();

        styleTitle.setWrapText(true);
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);

        styleTitle.setBorderTop(tittle);
        styleTitle.setBorderBottom(tittle);
        styleTitle.setBorderRight(tittle);
        styleTitle.setBorderLeft(tittle);

        styleTitle.setTopBorderColor(black);
        styleTitle.setRightBorderColor(black);
        styleTitle.setBottomBorderColor(black);
        styleTitle.setLeftBorderColor(black);

        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 52, 94)));

        Sheet sheet = wb.getSheetAt(0);


//---------------------------------1 лист-------------------------------------------

        int rowIndex = 1;
        int CellIndex = 1;
        sheets.createRow(++rowIndex).createCell(CellIndex).setCellValue("Период");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Выполнено :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("в процессе :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Невыполнено :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Общее :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);


        CellIndex = 1;

        sheets.createRow(++rowIndex).createCell(CellIndex).setCellValue("Весь период");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(doneAll);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(doingAll);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(notDoneAll);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(totalAll);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);


        rowIndex++;
        CellIndex = 1;
        sheets.createRow(++rowIndex).createCell(CellIndex).setCellValue("Период");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Выполнено :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("в процессе :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Невыполнено :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Общее :");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);


        CellIndex = 1;

        sheets.createRow(++rowIndex).createCell(CellIndex).setCellValue(period);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(done);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(doing);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(notDone);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(total);
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);


        CellIndex = 0;
        rowIndex++;

        sheets.createRow(++rowIndex).createCell(CellIndex).setCellValue("#");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Дата создания");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("ФИО");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Сервис");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Вопрос");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Исполнитель");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Статус");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Время выполнения");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("БЦ");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue("Пояснение");
        sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);


        for (DataRec rec : task) {

            try {
                CellIndex = 0;
                Date dataadd = rec.getDate("dataadd");
                Date datadoing = rec.getDate("datadoing");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy | HH:mm");
                String datebegin = dateFormat.format(dataadd);
                String deadlines = dateFormat.format(datadoing);
                String name_empl = "не назначено";
                String clarification = "";
                long employee_id = 0;
                try {
                    employee_id = (Long) rec.get("employee_id");
                } catch (Exception e) {
                }

                if (employee_id != 0) {
                    name_empl = userDao.getByChatId(employee_id).getUser_name();
                }

                if (rec.get("clarification") != null) {
                    clarification = (String) rec.get("clarification");
                }

                sheets.createRow(++rowIndex).createCell(CellIndex).setCellValue(rec.getString("id_task"));
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(datebegin);
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(rec.getString("user_name"));
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(rec.getString("name_p"));
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(rec.getString("text_t"));
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(name_empl);
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(rec.getString("name_st"));
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(deadlines);
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(rec.getString("name_c"));
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
                sheets.getRow(rowIndex).createCell(++CellIndex).setCellValue(clarification);
                sheets.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            } catch (Exception e) {
                log.error(this.getClass(), e.getCause());
            }
        }
        for (int i = 0; i < 10; i++) {
            sheets.autoSizeColumn(i); // операция дорогая, нужно делать один раз
        }

        //---------------------------------2 лист oficce-------------------------------------------


        int rowIndex_of = 0;
        int CellIndex_of = 0;
        sheet_of.createRow(++rowIndex_of).createCell(CellIndex_of).setCellValue("# Заявка");
        sheet_of.getRow(rowIndex_of).getCell(CellIndex_of).setCellStyle(styleTitle);
        sheet_of.getRow(rowIndex_of).createCell(++CellIndex_of).setCellValue("Дата создания");
        sheet_of.getRow(rowIndex_of).getCell(CellIndex_of).setCellStyle(styleTitle);
        sheet_of.getRow(rowIndex_of).createCell(++CellIndex_of).setCellValue("Данные");
        sheet_of.getRow(rowIndex_of).getCell(CellIndex_of).setCellStyle(styleTitle);

        for (DataRec office : rentDao.getOfficeList()) {
            String date_office = dateFormats.format(office.get("data_creat"));
            CellIndex_of = 0;
            sheet_of.createRow(++rowIndex_of).createCell(CellIndex_of).setCellValue(office.getInt("id"));
            sheet_of.getRow(rowIndex_of).getCell(CellIndex_of).setCellStyle(style);
            sheet_of.getRow(rowIndex_of).createCell(++CellIndex_of).setCellValue(date_office);
            sheet_of.getRow(rowIndex_of).getCell(CellIndex_of).setCellStyle(style);
            sheet_of.getRow(rowIndex_of).createCell(++CellIndex_of).setCellValue((String) office.get("text_of"));
            sheet_of.getRow(rowIndex_of).getCell(CellIndex_of).setCellStyle(style);
        }
        // операция дорогая, нужно делать один раз
        for (int i = 0; i < 3; i++) {
            sheet_of.autoSizeColumn(i);
        }


        //---------------------------------3 лист Conference-------------------------------------------

        rowIndex = 0;
        CellIndex = 0;
        Holl.createRow(++rowIndex).createCell(CellIndex).setCellValue("# Заявка");
        Holl.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        Holl.getRow(rowIndex).createCell(++CellIndex).setCellValue("Дата создания");
        Holl.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        Holl.getRow(rowIndex).createCell(++CellIndex).setCellValue("Данные");
        Holl.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        SimpleDateFormat Date_holl = new SimpleDateFormat("dd.MM | HH:mm");
        SimpleDateFormat Date_holl2 = new SimpleDateFormat(" - HH:mm");


        for (DataRec holl : rentDao.getHollList()) {
            String begin = Date_holl.format(holl.get("datebegin"));
            String end = Date_holl2.format(holl.get("deadline"));
            String Time_holl = begin + end;
            CellIndex = 0;

            Holl.createRow(++rowIndex).createCell(CellIndex).setCellValue(holl.getInt("id"));
            Holl.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            Holl.getRow(rowIndex).createCell(++CellIndex).setCellValue(Time_holl);
            Holl.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            Holl.getRow(rowIndex).createCell(++CellIndex).setCellValue((String) holl.get("text_hol"));
            Holl.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        }
        // операция дорогая, нужно делать один раз
        for (int i = 0; i < 3; i++) {
            Holl.autoSizeColumn(i);
        }

        //---------------------------------4 лист Анкета-------------------------------------------
        int countCells = 0;
        rowIndex = 0;
        CellIndex = 0;
        questions.createRow(++rowIndex).createCell(CellIndex).setCellValue("Вопрос");
        questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        questions.getRow(rowIndex).createCell(++CellIndex).setCellValue("Варианты ответов");
        questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        questions.getRow(rowIndex).createCell(++CellIndex).setCellValue("Варианты ответов");
        questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        questions.getRow(rowIndex).createCell(++CellIndex).setCellValue("Варианты ответов");
        questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        questions.getRow(rowIndex).createCell(++CellIndex).setCellValue("Варианты ответов");
        questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        questions.getRow(rowIndex).createCell(++CellIndex).setCellValue("Варианты ответов");
        questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        if (countCells < CellIndex) { // берем максимальное значение
            countCells = CellIndex;
        }
        for (DataRec qs : questionnaireDao.getList()) {
            CellIndex = 0;
            questions.createRow(++rowIndex).createCell(CellIndex).setCellValue(qs.getString("text_q"));
            questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);

            for (DataRec variant : questionnaireDao.getVariant(qs.getInt("id"))) {
                questions.getRow(rowIndex).createCell(++CellIndex).setCellValue(variant.getString("text_v"));
                questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            }
            countCells = CellIndex;
            CellIndex = 0;

            int countAll = questionnaireDao.countQuestion(qs.getInt("id"),
                    new Timestamp(databegin.toDate().getTime()), new Timestamp(deadline.toDate().getTime()));
            questions.createRow(++rowIndex).createCell(CellIndex).setCellValue(countAll);
            questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);

            for (DataRec variant : questionnaireDao.getVariant(qs.getInt("id"))) {
                String count = String.valueOf(questionnaireDao.countVariant(variant.getInt("id"),
                        new Timestamp(databegin.toDate().getTime()), new Timestamp(deadline.toDate().getTime())));

                questions.getRow(rowIndex).createCell(++CellIndex).setCellValue(count);
                questions.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            }
            if (countCells < CellIndex) { // берем максимальное значение
                countCells = CellIndex;
            }

        }
        countCells++;
        // операция дорогая, нужно делать один раз
        for (int i = 0; i < countCells; i++) {
            questions.autoSizeColumn(i);
        }
        //---------------------------------5 лист Пользователи-------------------------------------------
        countCells = 0;
        rowIndex = 0;
        CellIndex = 0;
        users.createRow(++rowIndex).createCell(CellIndex).setCellValue("#");
        users.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        users.getRow(rowIndex).createCell(++CellIndex).setCellValue("Категория");
        users.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        users.getRow(rowIndex).createCell(++CellIndex).setCellValue("ФИО");
        users.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        users.getRow(rowIndex).createCell(++CellIndex).setCellValue("Номер телефона");
        users.getRow(rowIndex).getCell(CellIndex).setCellStyle(styleTitle);
        if (countCells < CellIndex) { // берем максимальное значение
            countCells = CellIndex;
        }
        for (DataRec user : userDao.UserListAll()) {

            CellIndex = 0;

            users.createRow(++rowIndex).createCell(CellIndex).setCellValue(user.getInt("row_number"));
            users.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            users.getRow(rowIndex).createCell(++CellIndex).setCellValue(user.getString("name_ct"));
            users.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            users.getRow(rowIndex).createCell(++CellIndex).setCellValue(user.getString("user_name"));
            users.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
            users.getRow(rowIndex).createCell(++CellIndex).setCellValue(user.getString("phone"));
            users.getRow(rowIndex).getCell(CellIndex).setCellStyle(style);
        }
        if (countCells < CellIndex) { // берем максимальное значение
            countCells = CellIndex;
        }
        countCells++;
        for (int i = 0; i < countCells; i++) {
            users.autoSizeColumn(i);
        }

        String pieChartFilePath = "C:\\mobil-" + new Date().getTime() + ".xlsx";
        FileOutputStream tables = new FileOutputStream(pieChartFilePath);
        wb.write(tables);
        File file = new File(pieChartFilePath);
        FileInputStream fileInputStream = new FileInputStream(file);


        bot.sendDocument(new SendDocument()
                .setChatId(chatId)
                .setNewDocument("statistic " + datebegins + " - " + deadliness + " .xlsx", fileInputStream));
        file.delete();
    }
}
