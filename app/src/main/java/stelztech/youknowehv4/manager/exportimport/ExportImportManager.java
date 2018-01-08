package stelztech.youknowehv4.manager.exportimport;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.OpenableColumns;
import android.util.Log;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import stelztech.youknowehv4.activities.MainActivityManager;
import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.carddeck.CardDeck;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.quiz.Quiz;
import stelztech.youknowehv4.utilities.DateUtilities;

/**
 * Created by alex on 2017-05-02.
 */


public final class ExportImportManager {


    public final static String TAG = "ExportImportManager";

    private final static String storingFolder = "/YouKnowEh/Export";

    private final static String EXPORT_TAG = "_YKHExport";
    private final static String EXPORT_QUIZ_TAG = "_YKHQuizExport";

    public final static int CREATE_NEW_DECK = -1;


    private static File saveCSVFile(Context context, String location, Deck deckToExport, List<Card> cardList) {

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
                CardDeck cardDeck = Database.mCardDeckDao.fetchCardDeckById(cardTemp.getCardId(), deckToExport.getDeckId());
                data[0] = cardTemp.getQuestion();
                data[1] = cardTemp.getAnswer();
                data[2] = cardTemp.getMoreInfo();
                if (!cardDeck.isReview() && !DateUtilities.isValidDate(cardDeck.getReviewToggleDate()))
                    data[3] = "" + CardDeck.REVIEW_TOGGLE_ID;
                else
                    data[3] = cardDeck.getReviewToggleDate();
                data[4] = cardTemp.getDateCreated();
                data[5] = cardTemp.getDateModified();

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

            } catch (Exception ignored) {
            }
        }


        return file;
    }


    public static boolean readCSV(Context context, Uri uri, CustomProgressDialog customProgressDialog, int deckId) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return false;
        }


        List<CardHolder> cardHolderList = new ArrayList<CardHolder>();
        String fileName = getFileName(context, uri);
        try {
            // Creating Input Stream


            if (fileName.contains(".")) {
                int index = fileName.indexOf(".");
                fileName = fileName.substring(0, index);


                if (customProgressDialog != null)
                    customProgressDialog.setDialogTitle("Importing '" + fileName + "'");
            }

            // Toast.makeText(context, fileName, Toast.LENGTH_SHORT);

            InputStream myInput = context.getContentResolver().openInputStream(uri);
            CSVReader reader = new CSVReader(new InputStreamReader(myInput));

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
//                System.out.println(nextLine[0] + nextLine[1] + "etc...");

                if (nextLine.length > 6) {
                    //Toast.makeText(context, "Invalid file format", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    String question = "";
                    String answer = "";
                    String note = "";
                    String reviewToggle = "";
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
                            reviewToggle = nextLine[3];
                        } else if (rowCounter == 4) {
                            dateCreated = nextLine[4];
                        } else if (rowCounter == 5) {
                            dateModified = nextLine[5];
                        }
                        rowCounter++;
                    }

                    if (question.isEmpty() || answer.isEmpty()) {
                        //Toast.makeText(context, "Invalid file format - two first column cannot be empty", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        cardHolderList.add(new CardHolder(question, answer, note, reviewToggle, dateCreated, dateModified));
                    }

                }


            }

        } catch (Exception e) {
//            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("ExportImportManager", e.toString());
            return false;
        }

        if (deckId == CREATE_NEW_DECK)
            deckId = Database.mDeckDao.createDeck(fileName);

        for (int i = 0; i < cardHolderList.size(); i++) {
            String question = cardHolderList.get(i).getQuestion();
            String answer = cardHolderList.get(i).getAnswer();
            String note = cardHolderList.get(i).getNote();
            String dateCreated = cardHolderList.get(i).getDateCreated();
            String dateModified = cardHolderList.get(i).getDateModified();
            int cardId = Database.mCardDao.createCard(question, answer, note, dateCreated, dateModified);
            Database.mCardDeckDao.createCardDeck(cardId, deckId);

            String date = cardHolderList.get(i).getReviewToggleDate();
            if (!date.isEmpty()) {
                boolean isValidDate = DateUtilities.isValidDate(date);

                if (isValidDate)
                    Database.mCardDeckDao.setReviewToggleDate(cardId, deckId, date);
                else if (date.equals("" + CardDeck.REVIEW_TOGGLE_ID) || date.toLowerCase().trim().equals("false")) {
                    Database.mCardDeckDao.changeCardReviewTime(cardId, deckId, CardDeck.REVIEW_TOGGLE_ID);
                }
            }

        }


        Database.mCardDeckDao.revalidateReviewCards();

        //Toast.makeText(context, "Deck \"" + fileName + "\" imported", Toast.LENGTH_SHORT).show();
        return true;
    }


    private static boolean exportAllFiles(Context context) {

        List<Deck> deckList = Database.mDeckDao.fetchAllDecks();
        List<File> fileList = new ArrayList<>();
        for (int counter = 0; counter < deckList.size(); counter++) {
            Deck deck = deckList.get(counter);
            List<Card> cardList = Database.mCardDeckDao.fetchCardsByDeckId(deck.getDeckId());
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

            String zipFile = sdCard.getAbsolutePath() + storingFolder + "/"
                    + Database.mUserDao.fetchActiveProfile().getProfileName() + EXPORT_TAG + ".zip";

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

    public static void readAllCSV(Context context, Uri uri, CustomProgressDialog customProgressDialog) {

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

            }

            zipinputstream.close();

            if (customProgressDialog != null)
                customProgressDialog.setMax(files.size());

            for (int i = 0; i < files.size(); i++) {
                readCSV(context, Uri.fromFile(files.get(i)), customProgressDialog, CREATE_NEW_DECK);

                if (customProgressDialog != null)
                    customProgressDialog.setProgress(i + 1);
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

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            context.startActivity(emailIntent);
        }
    }

    public static void exportAllToEmail(Context context) {

        exportAllFiles(context);

        File sdCard = Environment.getExternalStorageDirectory();
        String zipFile = sdCard.getAbsolutePath() + storingFolder + "/"
                + Database.mUserDao.fetchActiveProfile().getProfileName() + EXPORT_TAG + ".zip";
        File dir = new File(zipFile);

        Uri U = Uri.fromFile(dir);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_STREAM, U);

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        context.startActivity(emailIntent);

    }

    public static void exportAllProfilesToEmail(Context context) {

        int currentProfileId = Database.mUserDao.fetchActiveProfile().getProfileId();

        List<Profile> profileList = Database.mProfileDao.fetchAllProfiles();

        ArrayList<Uri> uris = new ArrayList<Uri>();

        for (Profile profile : profileList) {
            Database.mUserDao.setActiveProfile(profile.getProfileId());

            exportAllFiles(context);

            File sdCard = Environment.getExternalStorageDirectory();
            String zipFile = sdCard.getAbsolutePath() + storingFolder + "/"
                    + Database.mUserDao.fetchActiveProfile().getProfileName() + EXPORT_TAG + ".zip";
            File dir = new File(zipFile);

            uris.add(Uri.fromFile(dir));

        }

        Database.mUserDao.setActiveProfile(currentProfileId);

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uris);


        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            context.startActivity(emailIntent);

        } catch (Exception exception) {
            Toast.makeText(context, "Error - couldn't start the email activity", Toast.LENGTH_SHORT).show();
        }
    }


    public static void exportQuizHistoryToEmail(Context context, String name, List<Quiz> quizList) {

        File file = saveQuizHistoryCSVFile(context, name, quizList);

        if (file != null) {
            Uri U = Uri.fromFile(file);
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_STREAM, U);

            if (Build.VERSION.SDK_INT >= 24) {
                try {
                    Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    m.invoke(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            context.startActivity(emailIntent);
        }
    }

    private static File saveQuizHistoryCSVFile(Context context, String name, List<Quiz> quizList) {

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return null;
        }

        int numberOfQuizzes = quizList.size();

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + storingFolder);
        if (!dir.exists()) {
            dir.mkdirs();
            if (!dir.exists()) {
                Toast.makeText(context, "Give app permission to access storage to export: " + name, Toast.LENGTH_SHORT).show();
                return null;
            }
        }


        File file = new File(dir, name + "_QuizHistory.csv");
        FileOutputStream os = null;

        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file));

            String[] data = new String[7];
            for (int counter = 0; counter < numberOfQuizzes; counter++) {

                Quiz quizTemp = quizList.get(counter);
                data[0] = String.valueOf(quizTemp.getMode());
                data[1] = String.valueOf(quizTemp.isReverse());
                data[2] = String.valueOf(quizTemp.getTotalPassed());
                data[3] = String.valueOf(quizTemp.getTotalFailed());
                data[4] = String.valueOf(quizTemp.getTotalSkipped());
                data[5] = quizTemp.getDateCreated();
                data[6] = quizTemp.getDateFinished();

                writer.writeNext(data);

            }
            writer.close();


        } catch (IOException e) {
            Toast.makeText(context, "Failed to export: " + name, Toast.LENGTH_SHORT).show();
            return null;
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        } finally {
            try {
                if (null != os)
                    os.close();

            } catch (Exception ignored) {
            }
        }


        return file;
    }

    public static boolean readQuizHistoryCSV(Context context, Uri uri, CustomProgressDialog customProgressDialog) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            return false;
        }

        List<QuizHistoryHolder> quizHistoryHolderList = new ArrayList<QuizHistoryHolder>();
        String fileName = getFileName(context, uri);
        try {
            if (fileName.contains(".")) {
                int index = fileName.indexOf(".");
                fileName = fileName.substring(0, index);


                if (customProgressDialog != null)
                    customProgressDialog.setDialogTitle("Importing '" + fileName + "'");
            }


            InputStream myInput = context.getContentResolver().openInputStream(uri);
            CSVReader reader = new CSVReader(new InputStreamReader(myInput));

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                if (nextLine.length > 7) {
                    return false;
                } else {
                    String type = "";
                    String reverse = "";
                    String pass = "";
                    String failed = "";
                    String skipped = "";
                    String dateCreated = "";
                    String dateFinished = "";
                    int rowCounter = 0;
                    for (int i = 0; i < nextLine.length; i++) {
                        if (rowCounter == 0) {
                            type = nextLine[0];
                        } else if (rowCounter == 1) {
                            reverse = nextLine[1];
                        } else if (rowCounter == 2) {
                            pass = nextLine[2];
                        } else if (rowCounter == 3) {
                            failed = nextLine[3];
                        } else if (rowCounter == 4) {
                            skipped = nextLine[4];
                        } else if (rowCounter == 5) {
                            dateCreated = nextLine[5];
                        } else if (rowCounter == 6) {
                            dateFinished = nextLine[6];
                        }
                        rowCounter++;
                    }

                    // todo validation here

                    quizHistoryHolderList.add(new QuizHistoryHolder(type, reverse, pass, failed, skipped, dateCreated, dateFinished));

                }
            }

        } catch (Exception e) {
            Log.e("ExportImportManager", e.toString());
            return false;
        }

        for (int i = 0; i < quizHistoryHolderList.size(); i++) {
            QuizHistoryHolder quizHistoryHolderTemp = quizHistoryHolderList.get(i);

            Quiz.MODE mode = Quiz.MODE.valueOf(quizHistoryHolderTemp.getQuizMode());
            Quiz.STATE state = Quiz.STATE.FINISHED_QUIZ;
            boolean isReverse = !(quizHistoryHolderTemp.getOrientationReverse().toLowerCase().equals("false") || quizHistoryHolderTemp.getOrientationReverse().equals("0"));
            int numberPassed = Integer.parseInt(quizHistoryHolderTemp.getNumberPassed());
            int numberFailed = Integer.parseInt(quizHistoryHolderTemp.getNumberFailed());
            int numberSkipped = Integer.parseInt(quizHistoryHolderTemp.getNumberSkipped());

            String dateCreated = quizHistoryHolderTemp.getDateCreated();
            if (dateCreated.isEmpty() || !DateUtilities.isValidDate(dateCreated)) {
                dateCreated = DateUtilities.getDateNowString();
            }

            String dateFinished = quizHistoryHolderTemp.getDateFinished();
            if (dateFinished.isEmpty() || !DateUtilities.isValidDate(dateFinished)) {
                dateFinished = DateUtilities.getDateNowString();
            }
            Database.mQuizDao.createQuiz(mode, isReverse, numberPassed, numberFailed, numberSkipped, dateCreated, dateFinished, state);
        }

        return true;
    }

    public static void importDeck(Context context, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");   //xlxs only

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select Deck to import"),
                    MainActivityManager.IMPORT_RESULT);
            Toast.makeText(context, "Select a Deck to import", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    public static void importQuizHistory(Context context, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");   //xlxs only

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select Quiz to import"),
                    MainActivityManager.IMPORT_QUIZ_HISTORY_RESULT);
            Toast.makeText(context, "Select a Quiz to import", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    public static void importToExistingDeck(Context context, Activity activity, int deckId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.putExtra("deckIdToImport", deckId);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");   //xlxs only

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a Deck to import"),
                    MainActivityManager.IMPORT_TO_EXISTING_RESULT);
            Toast.makeText(context, "Select a Deck to import", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }

    }

    public static void importAllDecks(Context context, Activity activity) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");   //zipa only
//        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select Decks to import"),
                    MainActivityManager.IMPORT_ALL_RESULT);
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

}
