package stelztech.youknowehv4.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.CardDeck;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.model.Profile;

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

        String activeProfileId = getActiveProfile().getProfileId();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCard.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableCard.COLUMN_NAME_ARCHIVED + "=" + 0 + " AND "
                + DatabaseVariables.TableCard.COLUMN_NAME_PROFILE_ID + "=" + activeProfileId, null);
        List<Card> cardList = new ArrayList<Card>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                cardList.add(fetchCardFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        cardList = SortingStateManager.getInstance().sortCardList(cardList);

        return cardList;
    }

    public List<Deck> getDecks() {
        SQLiteDatabase db = database.getReadableDatabase();

        String activeProfileId = getActiveProfile().getProfileId();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableDeck.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableDeck.COLUMN_NAME_PROFILE_ID + "=" + activeProfileId, null);
        List<Deck> deckList = new ArrayList<Deck>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                deckList.add(fetchDeckFromCursor(cursor));
                cursor.moveToNext();
            }
        }

        deckList = SortingStateManager.getInstance().sortDeck(deckList);


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

        cards = SortingStateManager.getInstance().sortCardList(cards);

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

        decks = SortingStateManager.getInstance().sortDeck(decks);

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

    public List<Profile> getProfiles() {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableProfile.TABLE_NAME, null);

        // get all card-deck
        List<Profile> profiles = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                profiles.add(fetchProfileFromCursor(cursor));
                cursor.moveToNext();
            }
        }

        cursor.close();
        return profiles;
    }

    public Profile getProfileFromId(String profileId) {
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableProfile.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);
        Profile profile = null;

        if (cursor.moveToFirst()) {
            profile = (fetchProfileFromCursor(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return profile;
    }

    ////////////// DELETE //////////////

    public boolean deleteDeck(String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();

        List<Card> cards = getCardsFromDeck(deckId);
        // delete card-deck
        for (int counter = 0; counter < cards.size(); counter++) {
            deleteCardDeck(cards.get(counter).getCardId(), deckId);
        }

        // resets the positions when deleting a deck
        List<Deck> deckList = getDecks();
        int deckToDeletePosition = getDeckFromId(deckId).getPosition();
        ContentValues values = new ContentValues();

        for (int i = 0; i < deckList.size(); i++) {
            if (deckList.get(i).getPosition() > deckToDeletePosition) {
                values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deckList.get(i).getPosition() - 1);
                db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
                        DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckList.get(i).getDeckId(), null);
            }
        }


        return db.delete(DatabaseVariables.TableDeck.TABLE_NAME,
                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null) > 0;
    }


    public boolean deleteCard(String cardId) {
        SQLiteDatabase db = database.getReadableDatabase();

        return db.delete(DatabaseVariables.TableCard.TABLE_NAME, DatabaseVariables.TableCard.COLUMN_NAME_CARD_ID
                + "=" + cardId, null) > 0;
    }

    public boolean deleteCardDeck(String cardId, String deckId) {
        SQLiteDatabase db = database.getReadableDatabase();

        return db.delete(DatabaseVariables.TableCardDeck.TABLE_NAME,
                DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId + " AND "
                        + DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null) > 0;
    }

    public boolean deleteProfile(String profileId) {
        SQLiteDatabase db = database.getReadableDatabase();

        return db.delete(DatabaseVariables.TableProfile.TABLE_NAME, DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID
                + "=" + profileId, null) > 0;
    }

    ////////////// ARCHIVED //////////////

    public void toggleArchiveCard(String cardId){
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        boolean currentArchive = getCardFromId(cardId).isArchived();

        if(!currentArchive){
            List<Deck> decks = getDecksFromCard(cardId);
            // delete card-deck
            for (int counter = 0; counter < decks.size(); counter++) {
                deleteCardDeck(cardId, decks.get(counter).getDeckId());
            }
        }

        values.put(DatabaseVariables.TableCard.COLUMN_NAME_ARCHIVED, !currentArchive);
        db.update(DatabaseVariables.TableCard.TABLE_NAME, values,
                DatabaseVariables.TableCard.COLUMN_NAME_CARD_ID + "=" + cardId, null);
    }


    public List<Card> getArchivedCards()
    {
        SQLiteDatabase db = database.getReadableDatabase();

        String activeProfileId = getActiveProfile().getProfileId();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCard.TABLE_NAME + " WHERE "
                + DatabaseVariables.TableCard.COLUMN_NAME_ARCHIVED + "=" + 1 + " AND "
                + DatabaseVariables.TableCard.COLUMN_NAME_PROFILE_ID + "=" + activeProfileId, null);
        List<Card> cardList = new ArrayList<Card>();

        if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                cardList.add(fetchCardFromCursor(cursor));
                cursor.moveToNext();
            }
        }
        cursor.close();

        cardList = SortingStateManager.getInstance().sortCardList(cardList);

        return cardList;
    }


    ////////////// CREATE //////////////

    public String createDeck(String name) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        String activeProfileId = getActiveProfile().getProfileId();
        int position = getDecks().size() + 1;

        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME, name);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_CREATED, date);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED, date);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_PROFILE_ID, activeProfileId);
        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, position);


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

        String activeProfileId = getActiveProfile().getProfileId();


        values.put(DatabaseVariables.TableCard.COLUMN_NAME_QUESTION, question);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_ANSWER, answer);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_MORE_INFORMATION, moreInfo);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_DATE_CREATED, date);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_DATE_MODIFIED, date);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_PROFILE_ID, activeProfileId);
        values.put(DatabaseVariables.TableCard.COLUMN_NAME_ARCHIVED, false);

        long newRowId = -1;
        newRowId = db.insert(
                DatabaseVariables.TableCard.TABLE_NAME,
                null,
                values);
        return Long.toString(newRowId);
    }


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


    public String createProfile(String name) {
        SQLiteDatabase db = database.getWritableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_NAME, name);
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_CREATED, date);
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ACTIVE, false);
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_QUESTION_LABEL, "Question");
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ANSWER_LABEL, "Answer");


        long newRowId = -1;
        newRowId = db.insert(
                DatabaseVariables.TableProfile.TABLE_NAME,
                null,
                values);

        // set newly created profile to active
        setActiveProfile(Long.toString(newRowId));

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

    public void updateProfile(String profileId, String name) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_NAME, name);
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
        db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
                DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);

    }

    public void updateProfileQuestionLabel(String profileId, String questionLabel) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_QUESTION_LABEL, questionLabel);
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
        db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
                DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);
    }

    public void updateProfileAnswerLabel(String profileId, String answerLabel) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        String date = getDateNow();

        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ANSWER_LABEL, answerLabel);
        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
        db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
                DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);
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

    public void setActiveProfile(String profileId) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ACTIVE, true);
        db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
                DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);

        List<Profile> profileList = getProfiles();

        for (int i = 0; i < profileList.size(); i++) {
            Profile tempProfile = profileList.get(i);
            // setting other profiles to not selected
            if (!tempProfile.getProfileId().equals(profileId) && tempProfile.isActive()) {
                values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ACTIVE, false);
                db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
                        DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + tempProfile.getProfileId(), null);
            }
        }

    }

    public void swapDeckPosition(Deck deck1, Deck deck2) {
        SQLiteDatabase db = database.getReadableDatabase();
        ContentValues values = new ContentValues();


        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deck2.getPosition());
        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deck1.getDeckId(), null);

        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deck1.getPosition());
        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deck2.getDeckId(), null);

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
        boolean archived = cursor.getInt(cursor
                .getColumnIndex(DatabaseVariables.TableCard.COLUMN_NAME_ARCHIVED)) > 0;

        return new Card(id, question, answer, moreInfo, dateCreated, dateModified, archived);
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
        int position = cursor.getInt(cursor
                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION));

        return new Deck(id, name, dateCreated, dateModified, position);
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

    private Profile fetchProfileFromCursor(Cursor cursor) {
        String profileId = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID));
        String profileName = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_NAME));
        boolean profileSelected = cursor.getInt(cursor
                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_ACTIVE)) > 0;
        String dateAdded = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_CREATED));
        String questionLabel = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_QUESTION_LABEL));
        String answerLabel = cursor.getString(cursor
                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_ANSWER_LABEL));


        return new Profile(profileId, profileName, dateAdded, profileSelected, questionLabel, answerLabel);
    }

    private String getDateNow() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date now = Calendar.getInstance().getTime();
        return df.format(now);
    }

    public Profile getActiveProfile() {

        List<Profile> profileList = getProfiles();

        for (int i = 0; i < profileList.size(); i++) {
            Profile tempProfile = profileList.get(i);
            if (tempProfile.isActive()) {
                return tempProfile;
            }
        }

        return null;
    }


}
