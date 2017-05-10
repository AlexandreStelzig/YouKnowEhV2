package stelztech.youknowehv4.manager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;

/**
 * Created by alex on 2017-05-02.
 */


public final class ExportImportManager {


    public final static String TAG = "ExportImportManager";

    public final static String storingFolder = "/YouKnowEh/Export";


    public static File saveExcelFile(Context context, Deck deckToExport, List<Card> cardList) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return null;
        };

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        String deckName = deckToExport.getDeckName();
        int numberOfCards = cardList.size();

        if (numberOfCards < 1) {
            Toast.makeText(context, "Cannot export deck with no cards", Toast.LENGTH_SHORT).show();
            return null;
        }

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet(deckName);

        for (int counter = 0; counter < numberOfCards; counter++) {
            Row row = sheet1.createRow(counter);

            Card cardTemp = cardList.get(counter);
            String question = cardTemp.getQuestion();
            String answer = cardTemp.getAnswer();
            String moreinfo = cardTemp.getMoreInfo();
            String id = cardTemp.getCardId();

            c = row.createCell(0);
            c.setCellValue(question);
            c = row.createCell(1);
            c.setCellValue(answer);
            c = row.createCell(2);
            c.setCellValue(moreinfo);
//            c = row.createCell(3);
//            c.setCellValue(id);

        }


        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));


//        File file = new File(context.getExternalFilesDir(null), deckToExport.getDeckName());

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + storingFolder);
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                Toast.makeText(context, "Give app permission to access storage to export", Toast.LENGTH_SHORT).show();
                return null;
            }
        }


        File file = new File(dir, deckName + ".xlsx");
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
        } catch (IOException e) {
            Toast.makeText(context, "Give app permission to access storage to export", Toast.LENGTH_SHORT).show();
            return null;
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        } finally {
            try {
                if (null != os)
                    os.close();

            } catch (Exception ex) {
            }
        }


        return file;
    }

    private static void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while (rowIter.hasNext()) {
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while (cellIter.hasNext()) {
                    HSSFCell myCell = (HSSFCell) cellIter.next();
                    Log.d(TAG, "Cell Value: " + myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static void exportFileToEmail(Context context, Deck deckToExport, List<Card> cardList) {

        File file = saveExcelFile(context, deckToExport, cardList);

        if (file != null) {
            Uri U = Uri.fromFile(file);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_STREAM, U);


            context.startActivity(emailIntent);
        }
    }

    public static void importDeck(Context context, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");   //xlxs only
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                    MainActivityManager.EXPORT_RESULT);
            Toast.makeText(context, "Select a Deck to import", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

}
