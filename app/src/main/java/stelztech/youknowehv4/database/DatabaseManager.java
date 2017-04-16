package stelztech.youknowehv4.database;

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

import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.CardDeck;
import stelztech.youknowehv4.model.Deck;

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

    public List<Card> getCards() {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCard.TABLE_NAME, null);
        List<Card> cardList = new ArrayList<Card>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                cardList.add(fetchCardFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        cardList = sortAlphabetically_Card(cardList);

        return cardList;
    }

    public List<Deck> getDecks() {
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


    public Card getCardFromId(String cardId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCard.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableCard.COLUMN_NAME_CARD_ID + "=" + cardId, null);
        Card card = null;

        if (cursor.moveToFirst()) {
            card = fetchCardFromCursor(cursor);
            cursor.moveToNext();
        }
        cursor.close();
        return card;
    }

    public Deck getDeckFromId(String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
        Deck deck = null;

        if (cursor.moveToFirst()) {
            deck = (fetchDeckFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return deck;
    }

    public CardDeck getCardDeck(String cardId, String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE " +
                DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId + " AND "
                + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
        CardDeck cardDeck = null;

        if (cursor.moveToFirst()) {
            cardDeck = (fetchCardDeckFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return cardDeck;
    }


    public List<Card> getCardsFromDeck(String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);

        // get all card-deck
        List<CardDeck> cardDecks = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                cardDecks.add(fetchCardDeckFromCursor(cursor));
                cursor.moveToNext();
            }
        }

        // get all cards
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            cards.add(getCardFromId(cardDecks.get(i).getCardId()));
        }

        cards = sortAlphabetically_Card(cards);

        cursor.close();
        return cards;
    }

    public List<Deck> getDecksFromCard(String cardId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId, null);

        // get all card-deck
        List<CardDeck> cardDecks = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                cardDecks.add(fetchCardDeckFromCursor(cursor));
                cursor.moveToNext();
            }
        }

        // get all decks
        List<Deck> decks = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            decks.add(getDeckFromId(cardDecks.get(i).getDeckId()));
        }

        decks = sortAlphabetically_Deck(decks);

        cursor.close();
        return decks;
    }


    public List<Card> getDeckPracticeCards(String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);

        // get all card-deck
        List<CardDeck> cardDecks = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                cardDecks.add(fetchCardDeckFromCursor(cursor));
                cursor.moveToNext();
            }
        }

        // get all cards isPractice
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            if (cardDecks.get(i).isPractice())
                cards.add(getCardFromId(cardDecks.get(i).getCardId()));
        }

        cursor.close();
        return cards;

    }


    ////////////// DELETE //////////////

    public boolean deleteDeck(String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();

        List<Card> cards = getCardsFromDeck(deckId);
        // delete card-deck
        for (int counter = 0; counter < cards.size(); counter++) {
            deleteCardDeck(cards.get(counter).getCardId(), deckId);
        }

        return db.delete(DatabaseVariables.TableDeck.TABLE_NAME,
                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null) > 0;
    }


    public boolean deleteCard(String cardId) {
        SQLiteDatabase db = database.getReadableDatabase();

        List<Deck> decks = getDecksFromCard(cardId);
        // delete card-deck
        for (int counter = 0; counter < decks.size(); counter++) {
            deleteCardDeck(cardId, decks.get(counter).getDeckId());
        }

        return db.delete(DatabaseVariables.TableCard.TABLE_NAME, DatabaseVariables.TableCard.COLUMN_NAME_CARD_ID
                + "=" + cardId, null) > 0;
    }

    public boolean deleteCardDeck(String cardId, String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();

        return db.delete(DatabaseVariables.TableCardDeck.TABLE_NAME,
                DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId + " AND "
                        + DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null) > 0;
    }

    ////////////// CREATE //////////////

    public String createDeck(String name) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME, name);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_CREATED, date);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED, date);



        long newRowId = -1;
        newRowId = db.insert(
                DatabaseVariables.TableDeck.TABLE_NAME,
                null,
                values);
        return Long.toString(newRowId);
    }


    public String createCard(String question, String answer, String moreInfo) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();


        values.put(DatabaseVariables.TableCard.COLUMN_NAME_QUESTION, question);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_ANSWER, answer);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_MORE_INFORMATION, moreInfo);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_DATE_CREATED, date);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_DATE_MODIFIED, date);

        long newRowId = -1;
        newRowId = db.insert(
                DatabaseVariables.TableCard.TABLE_NAME,
                null,
                values);
        return Long.toString(newRowId);
    }

//    public String createCard(String question, String answer, String moreInfo, String deckId) {
//        String cardId = createCard(question, answer, moreInfo);
//        createCardDeck(cardId + "", deckId);
//        return cardId;
//    }


    public String createCardDeck(String cardId, String deckId) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID, deckId);
        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID, cardId);
        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_DATE_ADDED, date);
        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_IS_PRACTICE, 1);

        long newRowId = -1;
        newRowId = db.insert(
                DatabaseVariables.TableCardDeck.TABLE_NAME,
                null,
                values);
        return Long.toString(newRowId);
    }


    ////////////// UPDATE //////////////

    public void updateDeck(String deckId, String name) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME, name);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED, date);
        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);

    }


    public void updateCard(String cardId, String question, String answer, String moreInfo) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableCard.COLUMN_NAME_QUESTION, question);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_ANSWER, answer);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_MORE_INFORMATION, moreInfo);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_DATE_MODIFIED, date);
        db.update(DatabaseVariables.TableCard.TABLE_NAME, values,
                DatabaseVariables.TableCard.COLUMN_NAME_CARD_ID + "=" + cardId, null);
    }


    ////////////// OTHER //////////////

    public void togglePractice_Card(String cardId, String deckId) {

        CardDeck cardDeck = getCardDeck(cardId, deckId);

        boolean isPracticeCurrent = cardDeck.isPractice();
        boolean newIsPractice = !isPracticeCurrent;

        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_IS_PRACTICE, newIsPractice);
        db.update(DatabaseVariables.TableCardDeck.TABLE_NAME, values, DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID
                + "=" + cardId + " AND " + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);

    }


    ////////////// HELPERS //////////////

    private Card fetchCardFromCursor(Cursor cursor) {

        String id = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCard.COLUMN_NAME_CARD_ID));
        String question = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCard.COLUMN_NAME_QUESTION));
        String answer = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCard.COLUMN_NAME_ANSWER));
        String moreInfo = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCard.COLUMN_NAME_MORE_INFORMATION));
        String dateCreated = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCard.COLUMN_NAME_DATE_CREATED));
        String dateModified = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCard.COLUMN_NAME_DATE_MODIFIED));
        return new Card(id, question, answer, moreInfo, dateCreated, dateModified);
    }

    private Deck fetchDeckFromCursor(Cursor cursor) {
        String id = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID));
        String name = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME));
        String dateCreated = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_CREATED));
        String dateModified = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED));

        return new Deck(id, name, dateCreated, dateModified);
    }


    private CardDeck fetchCardDeckFromCursor(Cursor cursor) {
        String deckId = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID));
        String cardId = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID));
        boolean isPractice = cursor.getInt(cursor
                .getColumnIndex(DatabaseVariables.TableCardDeck.COLUMN_NAME_IS_PRACTICE)) > 0;
        String dateAdded = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableCardDeck.COLUMN_NAME_DATE_ADDED));


        return new CardDeck(deckId, cardId, isPractice, dateAdded);
    }

    private List<Card> sortAlphabetically_Card(List<Card> list) {

        Collections.sort(list, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {

                String o1String = o1.getQuestion().toLowerCase();
                String o2String = o2.getQuestion().toLowerCase();

                String o1StringPart = o1String.replaceAll("\\d", "");
                String o2StringPart = o2String.replaceAll("\\d", "");


                if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
                    return extractInt(o1String) - extractInt(o2String);
                }
                return o1String.compareTo(o2String);
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
            public int compare(Deck o1, Deck o2) {

                String o1String = o1.getDeckName().toLowerCase();
                String o2String = o2.getDeckName().toLowerCase();


                String o1StringPart = o1String.replaceAll("\\d", "");
                String o2StringPart = o2String.replaceAll("\\d", "");


                if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
                    return extractInt(o1String) - extractInt(o2String);
                }
                return o1String.compareTo(o2String);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        return list;

    }

    private String getDateNow() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date now = Calendar.getInstance().getTime();
        return df.format(now);
    }


}
