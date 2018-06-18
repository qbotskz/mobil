package components;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSheet {

    private static final String APPLICATION_NAME = "AkimatSaryarka";
    private static final JsonFactory JSON_FACTORY = new GsonFactory();
    private static HttpTransport httpTransport;
    private static final List<String> SPREADSHEET_SCOPES = Arrays.asList(SheetsScopes.SPREADSHEETS);
    private String spreadsheetId;


    public GoogleSheet(String spreadsheetId) {

        this.spreadsheetId = spreadsheetId;
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }


    private Sheets getSheetsService() throws IOException {
        InputStream stream = GoogleSheet.class.getResourceAsStream("/AkimatSaryarka-65c8e7aefc22.json");
        Credential credential = GoogleCredential.fromStream(stream)
                .createScoped(SPREADSHEET_SCOPES);
        return new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    public void write(
            int startRow,
            String sheetName,
            String startCol,
            String endCol,
            List<List<Object>> data
    ){

        try {


            Sheets sheets = getSheetsService();
            String range =  sheetName + "!" + startCol + startRow + ":" + endCol;

            ValueRange vr = new ValueRange().setValues(data)
                    .setMajorDimension("ROWS");

            sheets.spreadsheets().values()
                    .update(spreadsheetId, range, vr)
                    .setValueInputOption("RAW")
                    .execute();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public List<List<Object>> getValues(
            String sheetName,
            String startCol,
            String endCol
    ){

        List<List<Object>> lists = new ArrayList<>();

        try {

            Sheets sheets = getSheetsService();
            int lastRowNumber = 1;
            String range = sheetName + "!" + startCol + lastRowNumber + ":" + endCol;

            ValueRange response = sheets.spreadsheets().values()
                    .get(spreadsheetId, range).execute();
            lists = response.getValues();

        } catch (Exception e){
            e.printStackTrace();
        }

        return lists;
    }

}