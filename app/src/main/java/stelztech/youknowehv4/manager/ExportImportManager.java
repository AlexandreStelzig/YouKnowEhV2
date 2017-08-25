package stelztech.youknowehv4.manager;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import stelztech.youknowehv4.activitypackage.MainActivityManager;
import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.CardDeck;
import stelztech.youknowehv4.model.Deck;

/**
 * Created by alex on 2017-05-02.
 */


public final class ExportImportManager {


    public final static String TAG = "ExportImportManager";

    public final static String storingFolder = "/YouKnowEh/Export";


    public static File saveCSVFile(Context context, String location, Deck deckToExport, List<Card> cardList) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return null;
        }

        String deckName = deckToExport.getDeckName();
        int numberOfCards = cardList.size();

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + storingFolder);
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                Toast.makeText(context, "Give app permission to access storage to export: " + deckName, Toast.LENGTH_SHORT).show();
                return null;
            }
        }


        File file = new File(dir, deckName + ".csv");
        FileOutputStream os = null;

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            String[] data = new String[6];
            for (int counter = 0; counter < numberOfCards; counter++) {

                Card cardTemp = cardList.get(counter);
                CardDeck cardDeck = DatabaseManager.getInstance(context).getCardDeck(cardTemp.getCardId(), deckToExport.getDeckId());
                data[0] = cardTemp.getQuestion();
                data[1] = cardTemp.getAnswer();
                data[2] = cardTemp.getMoreInfo();
                data[3] = "" + cardDeck.isPractice();
                data[4] = cardTemp.getDateCreated();
                data[5] = "" + cardTemp.getDateModified();

                writer.writeNext(data);

            }
            writer.close();


        } catch (IOException e) {
            Toast.makeText(context, "Failed to export: " + deckName, Toast.LENGTH_SHORT).show();
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


    public static boolean readCSV(Context context, Uri uri) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return false;
        }


        List<CardHolder> cardHolderList = new ArrayList<CardHolder>();
        String fileName = getFileName(context, uri);
        try {
            // Creating Input Stream


            if (fileName.contains(".")) {
                int index = fileName.indexOf(".");
                fileName = fileName.substring(0, index);
            }

            Toast.makeText(context, fileName, Toast.LENGTH_SHORT);

            InputStream myInput = context.getContentResolver().openInputStream(uri);
            CSVReader reader = new CSVReader(new InputStreamReader(myInput));

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                System.out.println(nextLine[0] + nextLine[1] + "etc...");

                if (nextLine.length > 6) {
                    Toast.makeText(context, "Invalid file format", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    String question = "";
                    String answer = "";
                    String note = "";
                    String isPractice = "";
                    String dateCreated = "";
                    String dateModified = "";
                    int rowCounter = 0;
                    for (int i = 0; i < nextLine.length; i++) {
                        if (rowCounter == 0) {
                            question = nextLine[0];
                        } else if (rowCounter == 1) {
                            answer = nextLine[1];
                        } else if (rowCounter == 2) {
                            note = nextLine[2];
                        } else if (rowCounter == 3) {
                            isPractice = nextLine[3];
                        } else if (rowCounter == 4) {
                            dateCreated = nextLine[4];
                        } else if (rowCounter == 5) {
                            dateModified = nextLine[5];
                        }
                        rowCounter++;
                    }

                    if (question.isEmpty() || answer.isEmpty()) {
                        Toast.makeText(context, "Invalid file format - two first column cannot be empty", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        cardHolderList.add(new CardHolder(question, answer, note, isPractice, dateCreated, dateModified));
                    }

                }


            }

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return false;
        }

        DatabaseManager dbManager = DatabaseManager.getInstance(context);
        String deckId = dbManager.createDeck(fileName);

        for (int i = 0; i < cardHolderList.size(); i++) {
            String question = cardHolderList.get(i).getQuestion();
            String answer = cardHolderList.get(i).getAnswer();
            String note = cardHolderList.get(i).getNote();
            String dateCreated = cardHolderList.get(i).getDateCreated();
            String dateModified = cardHolderList.get(i).getDateModified();
            String cardId = dbManager.createCard(question, answer, note, dateCreated, dateModified);
            dbManager.createCardDeck(cardId, deckId);

            if (cardHolderList.get(i).getIsPractice().trim().toLowerCase().equals("false") ||
                    cardHolderList.get(i).getIsPractice().equals("0")) {
                dbManager.togglePractice_Card(cardId, deckId, -1);
            }

        }

        Toast.makeText(context, "Deck \"" + fileName + "\" imported", Toast.LENGTH_SHORT).show();
        return true;
    }


    public static boolean exportAllFiles(Context context) {
        DatabaseManager database = DatabaseManager.getInstance(context);
        List<Deck> deckList = database.getDecks();
        List<File> fileList = new ArrayList<>();
        for (int counter = 0; counter < deckList.size(); counter++) {
            Deck deck = deckList.get(counter);
            List<Card> cardList = database.getCardsFromDeck(deck.getDeckId());
            File tempFile = saveCSVFile(context, "ToExport", deck, cardList);

            if (tempFile != null)
                fileList.add(tempFile);
        }

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + storingFolder);
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                Toast.makeText(context, "Give app permission to access storage to export all", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        try {

            String zipFile = sdCard.getAbsolutePath() + storingFolder + "/YouKnowEhExportedDecks.zip";

            // create byte buffer
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (int i = 0; i < fileList.size(); i++) {
                File srcFile = (fileList.get(i));
                FileInputStream fis = new FileInputStream(srcFile);

                // begin writing a new ZIP entry, positions the stream to the start of the entry data
                zos.putNextEntry(new ZipEntry(srcFile.getName()));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();

                // close the InputStream
                fis.close();
            }

            // close the ZipOutputStream
            zos.close();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }


        return true;
    }

    public static void readAllCSV(Context context, Uri uri) {

        try {

            InputStream myInput = context.getContentResolver().openInputStream(uri);

            // this is where you start, with an InputStream containing the bytes from the zip file
            ZipInputStream zipinputstream = new ZipInputStream(new BufferedInputStream(myInput));
            ZipEntry zipentry;

            byte[] buf = new byte[1024];
            zipentry = zipinputstream.getNextEntry();
            List<File> files = new ArrayList<>();

            while (zipentry != null) {
                //for each entry to be extracted
                String entryName = zipentry.getName();
                System.out.println("entryname " + entryName);

                String extension = "";

                int i = entryName.lastIndexOf('.');
                if (i > 0) {
                    extension = entryName.substring(i + 1);
                }

                if (!extension.equals("csv")) {
                    break;
                }

                int n;
                FileOutputStream fileoutputstream;
                File newFile = new File(entryName);
                String directory = newFile.getParent();

                if (directory == null) {
                    if (newFile.isDirectory())
                        break;
                }

                File sdCard = Environment.getExternalStorageDirectory();
                String destinationname = sdCard.getAbsolutePath() + "/YouKnowEh/Export/";
                fileoutputstream = new FileOutputStream(
                        destinationname + entryName);

                while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
                    fileoutputstream.write(buf, 0, n);

                files.add(new File(destinationname + entryName));


                fileoutputstream.close();
                zipinputstream.closeEntry();
                zipentry = zipinputstream.getNextEntry();

            }//while

            zipinputstream.close();


            for (int i = 0; i < files.size(); i++) {
                readCSV(context, Uri.fromFile(files.get(i)));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void exportFileToEmail(Context context, Deck deckToExport, List<Card> cardList) {

        File file = saveCSVFile(context, "", deckToExport, cardList);

        if (file != null) {
            Uri U = Uri.fromFile(file);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_STREAM, U);


            context.startActivity(emailIntent);
        }
    }

    public static void exportAllToEmail(Context context) {

        exportAllFiles(context);

        File sdCard = Environment.getExternalStorageDirectory();
        String zipFile = sdCard.getAbsolutePath() + storingFolder + "/YouKnowEhExportedDecks.zip";
        File dir = new File(zipFile);

        Uri U = Uri.fromFile(dir);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_STREAM, U);


        context.startActivity(emailIntent);

    }

    public static void importDeck(Context context, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");   //xlxs only
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                    MainActivityManager.EXPORT_RESULT);
            Toast.makeText(context, "Select a Deck to import", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    public static void importAllDecks(Context context, Activity activity) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");   //xlxs only
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"),
                    MainActivityManager.EXPORT_RESULT_ALL);
            Toast.makeText(context, "Select a zip file with Decks to import", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getFileName(Context context, Uri uri) {
        String fileName = "";
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = uri.getLastPathSegment();
        }
        if (fileName == null) {
            fileName = uri.getPath();
            int cut = fileName.lastIndexOf('/');
            if (cut != -1) {
                fileName = fileName.substring(cut + 1);
            }
        }

        return fileName;

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


    private static class CardHolder {
        private String question;
        private String answer;
        private String note;
        private String isPractice;
        private String dateCreated;
        private String dateModified;

        public CardHolder(String question, String answer, String note, String isPractice, String dateCreated, String dateModified) {
            this.question = question;
            this.answer = answer;
            this.note = note;
            this.isPractice = isPractice;
            this.dateCreated = dateCreated;
            this.dateModified = dateModified;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getDateModified() {
            return dateModified;
        }

        public void setDateModified(String dateModified) {
            this.dateModified = dateModified;
        }

        public String getIsPractice() {
            return isPractice;
        }

        public void setIsPractice(String isPractice) {
            this.isPractice = isPractice;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
