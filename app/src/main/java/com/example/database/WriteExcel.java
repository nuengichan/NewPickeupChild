package com.example.database;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.example.database.models.Post;
import com.example.database.models.Topic;
import com.google.firebase.database.ServerValue;

import java.io.File;
import java.io.IOException;
import java.security.Timestamp;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


/**
 * Created by kedkamon on 2/23/2017 AD.
 */

public class WriteExcel {

    private Context mContext;
    private File mFile;
    private ExcelObject mExcelObject;
    public WriteExcel() {

    }

    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String inputFile;

    public void setOutputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void write() throws IOException, WriteException {
        File file = mFile;
        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
        workbook.createSheet("Report", 0);
        WritableSheet excelSheet = workbook.getSheet(0);
        createLabel(excelSheet);
        createContent(excelSheet);

        workbook.write();
        workbook.close();


        Uri path = Uri.fromFile(mFile);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// set the type to 'email'
        emailIntent .setType("vnd.android.cursor.dir/email");
        String to[] = {"mondayishappy26@gmail.com"};
        //emailIntent .putExtra(Intent.EXTRA_EMAIL, to);
// the attachment
        emailIntent .putExtra(Intent.EXTRA_STREAM, path);
// the mail subject
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Subject: "+mExcelObject.getActivityName());
        mContext.startActivity(Intent.createChooser(emailIntent , "Send email..."));
    }

    private void createLabel(WritableSheet sheet)
            throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);
        // Lets automatically wrap the cells
        times.setWrap(true);

        // create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(
                WritableFont.TIMES, 10, WritableFont.BOLD, true,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);

//        addCaption(sheet, 3, 0, mExcelObject.getHourActivity()); // ชั่วโมง

        // Write a few headers
//        addCaption(sheet, 0, 0, "ชื่อกิจกรรม "+mExcelObject.getActivityName()); // ชื่อกิจกรรม
//        addCaption(sheet, 1, 0, "วันที่จัด "+mExcelObject.getTimeStart());  // วันที่จัด
//        addCaption(sheet, 2, 0, "สถานที่่่ "+mExcelObject.getLocationName()); // สถานที่





        addCaption(sheet, 0, 1, "ลำดับ");
        addCaption(sheet, 1, 1, "ชื่อผู้ปกครอง");
        addCaption(sheet, 2, 1, "ชื่อเด็กนักเรียน");
        addCaption(sheet, 3, 1, "ระดับชั้น");
        addCaption(sheet, 4, 1, "เวลามารับ");





    }

    private void createContent(WritableSheet sheet) throws WriteException,
            RowsExceededException {
        // Write a few number
        int row = 2;
        for (int i = 0; i < mExcelObject.getmCheckInUserNews().size(); i++) {
            // First column
            Post data = mExcelObject.getmCheckInUserNews().get(i);
            addNumber(sheet, 0, row, i+1 );
            // Second column
            addLabel(sheet, 1, row, data.author());
            addLabel(sheet, 2, row, data.title);
            addLabel(sheet, 3, row, data.body);
            addLabel(sheet, 4, row, data.downloadeUrl);
            row++;
        }

    }

    private void addCaption(WritableSheet sheet, int column, int row, String s)
            throws RowsExceededException, WriteException {
        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);
    }

    private void addNumber(WritableSheet sheet, int column, int row,
                           Integer integer) throws WriteException, RowsExceededException {
        Number number;
        number = new Number(column, row, integer, times) {
        };
        sheet.addCell(number);
    }

    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    public void writeExcel(Context context, ExcelObject excelObject) {
        mContext = context;
        mExcelObject = excelObject;
        File mDir = new File(Environment.getExternalStorageDirectory().getPath() +"/Time_Repoet/");
        if(mDir.exists() && mDir.isDirectory()) {
            // do something here
            mFile = new File(mDir, "Repoet.xls");
            try {
                write();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mDir.mkdir();
            mFile = new File(mDir, "Repoet.xls");
            try {
                write();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}