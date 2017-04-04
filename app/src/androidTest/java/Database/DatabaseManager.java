package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import Model.Deck;
import Model.Word;
import Model.WordDeck;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class DatabaseManager {

    private static DatabaseManager databaseManager = null;
    private Database database;


    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }


    DatabaseManager(Context context) {
        database = new Database(context);
    }

    ////////////// GET //////////////

    public List<Word> getWords() {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableWord.TABLE_NAME, null);
        List<Word> wordList = new ArrayList<Word>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                wordList.add(fetchWordFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        wordList = sortAlphabetically_Word(wordList);

        return wordList;
    }

    public List<Deck> getDeck() {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableDeck.TABLE_NAME, null);
        List<Deck> deckList = new ArrayList<Deck>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                deckList.add(fetchDeckFromCursor(cursor));
                cursor.moveToNext();
            }
        }

        deckList = sortAlphabetically_Deck(deckList);


        cursor.close();
        return deckList;
    }


    public Word getWordFromId(String idWord) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableWord.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableWord.COLUMN_NAME_ID_WORD + "=" + idWord, null);
        Word word = null;

        if (cursor.moveToFirst()) {
            word = fetchWordFromCursor(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return word;
    }

    public Deck getDeckFromId(String idDeck) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null);
        Deck deck = null;

        if (cursor.moveToFirst()) {
            deck = (fetchDeckFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return deck;
    }

    public WordDeck getWordDeck(String idWord, String idDeck) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableWordDeck.TABLE_NAME + " WHERE " +
                DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_WORD + "=" + idWord + " AND "
                + DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null);
        WordDeck wordDeck = null;

        if (cursor.moveToFirst()) {
            wordDeck = (fetchWordDeckFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return wordDeck;
    }


    public List<Word> getWordsFormDeck(String idDeck) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableWordDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null);

        // get all word-deck
        List<WordDeck> wordDecks = null;

        if (cursor.moveToFirst()) {
            wordDecks.add(fetchWordDeckFromCursor(cursor));
            cursor.moveToNext();
        }

        // get all words
        List<Word> words = null;
        for (int i = 0; i < wordDecks.size(); i++) {
            words.add(getWordFromId(wordDecks.get(i).getIdWord()));
        }

        words = sortAlphabetically_Word(words);

        cursor.close();
        return words;
    }

    public List<Deck> getDecksFromWord(String idWord) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableWordDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_WORD + "=" + idWord, null);

        // get all word-deck
        List<WordDeck> wordDecks = null;

        if (cursor.moveToFirst()) {
            wordDecks.add(fetchWordDeckFromCursor(cursor));
            cursor.moveToNext();
        }

        // get all decks
        List<Deck> decks = null;
        for (int i = 0; i < wordDecks.size(); i++) {
            decks.add(getDeckFromId(wordDecks.get(i).getIdDeck()));
        }

        decks = sortAlphabetically_Deck(decks);

        cursor.close();
        return decks;
    }


    public List<Word> getDeckPracticeWords(String idDeck) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableWordDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null);

        // get all word-deck
        List<WordDeck> wordDecks = null;

        if (cursor.moveToFirst()) {
            wordDecks.add(fetchWordDeckFromCursor(cursor));
            cursor.moveToNext();
        }

        // get all words isPractice
        List<Word> words = null;
        for (int i = 0; i < wordDecks.size(); i++) {
            if(wordDecks.get(i).isPractice())
                words.add(getWordFromId(wordDecks.get(i).getIdWord()));
        }

        cursor.close();
        return words;

    }


    ////////////// DELETE //////////////

    public boolean deleteDeck(String idDeck) {
        SQLiteDatabase db = database.getReadableDatabase();

        List<Word> words = getWordsFormDeck(idDeck);
        // delete word-deck
        for (int counter = 0; counter < words.size(); counter++) {
            deleteWordDeck(words.get(counter).getIdWord(), idDeck);
        }

        return db.delete(DatabaseVariables.TableDeck.TABLE_NAME,
                DatabaseVariables.TableDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null) > 0;
    }


    public boolean deleteWord(String idWord) {
        SQLiteDatabase db = database.getReadableDatabase();

        List<Deck> decks = getDecksFromWord(idWord);
        // delete word-deck
        for (int counter = 0; counter < decks.size(); counter++) {
            deleteWordDeck(idWord, decks.get(counter).getIdDeck());
        }

        return db.delete(DatabaseVariables.TableWord.TABLE_NAME, DatabaseVariables.TableWord.COLUMN_NAME_ID_WORD
                + "=" + idWord, null) > 0;
    }

    public boolean deleteWordDeck(String idWord, String idDeck) {
        SQLiteDatabase db = database.getReadableDatabase();
        return db.delete(DatabaseVariables.TableWordDeck.TABLE_NAME,
                DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_WORD + "=" + idWord + " AND "
                        + DatabaseVariables.TableDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null) > 0;
    }

    ////////////// CREATE //////////////

    public long createDeck(String name) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME, name);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_CREATED, date);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED, date);

        long newRowId;
        newRowId = db.insert(
                DatabaseVariables.TableDeck.TABLE_NAME,
                null,
                values);
        return newRowId;
    }

    public long createDeck(String name, String idWord){
        long idDeck = createDeck(name) ;
        createWordDeck(idWord, idDeck+"");
        return idDeck;
    }


    public long createWord(String question, String answer, String moreInfo) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();


        values.put(DatabaseVariables.TableWord.COLUMN_NAME_QUESTION, question);
        values.put(DatabaseVariables.TableWord.COLUMN_NAME_ANSWER, answer);
        values.put(DatabaseVariables.TableWord.COLUMN_NAME_MORE_INFORMATION, moreInfo);
        values.put(DatabaseVariables.TableWord.COLUMN_NAME_DATE_CREATED, date);
        values.put(DatabaseVariables.TableWord.COLUMN_NAME_DATE_MODIFIED, date);

        long newRowId;
        newRowId = db.insert(
                DatabaseVariables.TableWord.TABLE_NAME,
                null,
                values);
        return newRowId;
    }

    public long createWord(String question, String answer, String moreInfo, String idDeck) {
        long idWord = createWord(question,answer,moreInfo);
        createWordDeck(idWord+"",idDeck);
        return idWord;
    }


    public long createWordDeck(String idWord, String idDeck){
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_DECK, idDeck);
        values.put(DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_WORD, idWord);
        values.put(DatabaseVariables.TableWordDeck.COLUMN_NAME_DATE_ADDED, date);
        values.put(DatabaseVariables.TableWordDeck.COLUMN_NAME_IS_PRACTICE, 1);

        long newRowId;
        newRowId = db.insert(
                DatabaseVariables.TableWordDeck.TABLE_NAME,
                null,
                values);
        return newRowId;
    }


    ////////////// UPDATE //////////////

    public void updateDeck(String idDeck, String name) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME, name);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED, date);
        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
                DatabaseVariables.TableDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null);

    }


    public void updateWord(String idWord, String question, String answer, String moreInfo) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableWord.COLUMN_NAME_QUESTION, question);
        values.put(DatabaseVariables.TableWord.COLUMN_NAME_ANSWER, answer);
        values.put(DatabaseVariables.TableWord.COLUMN_NAME_MORE_INFORMATION, moreInfo);
        values.put(DatabaseVariables.TableWord.COLUMN_NAME_DATE_MODIFIED, date);
        db.update(DatabaseVariables.TableWord.TABLE_NAME, values,
                DatabaseVariables.TableWord.COLUMN_NAME_ID_WORD + "=" + idWord, null);
    }

    ////////////// OTHER //////////////

    public void togglePractice_Word(String idWord, String idDeck) {

        WordDeck wordDeck = getWordDeck(idWord, idDeck);

        boolean isPracticeCurrent = wordDeck.isPractice();
        boolean newIsPractice = !isPracticeCurrent;

        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseVariables.TableWordDeck.COLUMN_NAME_IS_PRACTICE, newIsPractice);
        db.update(DatabaseVariables.TableWordDeck.TABLE_NAME, values, DatabaseVariables.TableWord.COLUMN_NAME_ID_WORD
                + "=" + idWord + " AND " + DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_DECK + "=" + idDeck, null);

    }


    ////////////// HELPERS //////////////

    private Word fetchWordFromCursor(Cursor cursor) {

        String id = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWord.COLUMN_NAME_ID_WORD));
        String question = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWord.COLUMN_NAME_QUESTION));
        String answer = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWord.COLUMN_NAME_ANSWER));
        String moreInfo = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWord.COLUMN_NAME_MORE_INFORMATION));
        String dateCreated = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWord.COLUMN_NAME_DATE_CREATED));
        String dateModified = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWord.COLUMN_NAME_DATE_MODIFIED));
        return new Word(id, question, answer, moreInfo, dateCreated, dateModified);
    }

    private Deck fetchDeckFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_ID_DECK));
        String name = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME));
        String dateCreated = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_CREATED));
        String dateModified = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED));

        return new Deck(id, name, dateCreated, dateModified);
    }


    private WordDeck fetchWordDeckFromCursor(Cursor cursor) {
        String idDeck = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_DECK));
        String idWord = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWordDeck.COLUMN_NAME_ID_WORD));
        boolean isPractice = cursor.getInt(cursor
                .getColumnIndex(DatabaseVariables.TableWordDeck.COLUMN_NAME_IS_PRACTICE)) > 0;
        String dateAdded = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableWordDeck.COLUMN_NAME_DATE_ADDED));


        return new WordDeck(idDeck, idWord, isPractice, dateAdded);
    }

    private List<Word> sortAlphabetically_Word(List<Word> list) {

        Collections.sort(list, new Comparator<Word>() {
            @Override
            public int compare(Word lhs, Word rhs) {
                return extractInt(lhs.getQuestion()) - extractInt(rhs.getQuestion());
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });

        return list;

    }

    private List<Deck> sortAlphabetically_Deck(List<Deck> list) {

        Collections.sort(list, new Comparator<Deck>() {
            @Override
            public int compare(Deck lhs, Deck rhs) {
                return extractInt(lhs.getDeckName()) - extractInt(rhs.getDeckName());
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        return list;

    }

    private String getDateNow(){
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date now = Calendar.getInstance().getTime();
        return df.format(now);
    }


}
