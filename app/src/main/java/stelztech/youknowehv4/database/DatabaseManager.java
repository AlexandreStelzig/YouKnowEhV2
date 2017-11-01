package stelztech.youknowehv4.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.manager.SortingStateManager;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.carddeck.CardDeck;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;
import stelztech.youknowehv4.database.user.User;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class DatabaseManager {

    ////////////// GET //////////////


//    public List<Deck> getDecks() {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//        String activeProfileId = getActiveProfile().getProfileId();
//
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableDeck.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableDeck.COLUMN_NAME_PROFILE_ID + "=" + activeProfileId, null);
//        List<Deck> deckList = new ArrayList<Deck>();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//                deckList.add(fetchDeckFromCursor(cursor));
//                cursor.moveToNext();
//            }
//        }
//
//        deckList = SortingStateManager.getInstance().sortDeck(deckList);
//
//
//        cursor.close();
//        return deckList;
//    }

//    public User getUser() {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableUser.TABLE_NAME, null);
//        User user = null;
//
//        if (cursor.moveToFirst()) {
//            user = (fetchUserFromCursor(cursor));
//            cursor.moveToNext();
//
//        }
//
//        cursor.close();
//        return user;
//    }


//    public Deck getDeckFromId(String deckId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableDeck.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
//        Deck deck = null;
//
//        if (cursor.moveToFirst()) {
//            deck = (fetchDeckFromCursor(cursor));
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return deck;
//    }

//    public CardDeck getCardDeck(String cardId, String deckId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE " +
//                DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId + " AND "
//                + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
//        CardDeck cardDeck = null;
//
//        if (cursor.moveToFirst()) {
//            cardDeck = (fetchCardDeckFromCursor(cursor));
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return cardDeck;
//    }

//
//    public List<Card> getCardsFromDeck(String deckId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
//
//        // get all card-deck
//        List<CardDeck> cardDecks = new ArrayList<>();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//                cardDecks.add(fetchCardDeckFromCursor(cursor));
//                cursor.moveToNext();
//            }
//        }
//
//        // get all cards
//        List<Card> cards = new ArrayList<>();
//        for (int i = 0; i < cardDecks.size(); i++) {
//            cards.add(getCardFromId(cardDecks.get(i).getCardId()));
//        }
//
//        cards = SortingStateManager.getInstance().sortCardList(cards);
//
//        cursor.close();
//        return cards;
//    }

//    public List<Deck> getDecksFromCard(String cardId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId, null);
//
//        // get all card-deck
//        List<CardDeck> cardDecks = new ArrayList<>();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//                cardDecks.add(fetchCardDeckFromCursor(cursor));
//                cursor.moveToNext();
//            }
//        }
//
//        // get all decks
//        List<Deck> decks = new ArrayList<>();
//        for (int i = 0; i < cardDecks.size(); i++) {
//            decks.add(getDeckFromId(cardDecks.get(i).getDeckId()));
//        }
//
//        decks = SortingStateManager.getInstance().sortDeck(decks);
//
//        cursor.close();
//        return decks;
//    }

//    public void verifyPracticeCards() {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME, null);
//
//        // get all card-deck
//        List<CardDeck> cardDecks = new ArrayList<>();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//                cardDecks.add(fetchCardDeckFromCursor(cursor));
//                cursor.moveToNext();
//            }
//        }
//
//        List<CardDeck> cardDecksNotPractice = new ArrayList<>();
//        for (int i = 0; i < cardDecks.size(); i++) {
//            if (!cardDecks.get(i).isPractice())
//                cardDecksNotPractice.add(cardDecks.get(i));
//        }
//
//        cardDecks.clear();
//
//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//        Date now = Calendar.getInstance().getTime();
//
//
//        for (int counter = 0; counter < cardDecksNotPractice.size(); counter++) {
//
//            CardDeck temp = cardDecksNotPractice.get(counter);
//            String dateToggle = temp.getPracticeToggleDate();
//
//            if (!dateToggle.equals("")) {
//
//                Date date = null;
//                try {
//                    date = df.parse(dateToggle);
//
//                    boolean readyToToggle = now.after(date);
//
//                    if (readyToToggle) {
//                        togglePractice_Card(temp.getCardId(), temp.getDeckId(), -1);
//                    }
//
//                } catch (ParseException e) {
//                    Log.e("DATABASE", "error with date formatting");
//                }
//
//
//            }
//        }
//
//    }

//    public List<Card> getDeckPracticeCards(String deckId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
//
//        // get all card-deck
//        List<CardDeck> cardDecks = new ArrayList<>();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//                cardDecks.add(fetchCardDeckFromCursor(cursor));
//                cursor.moveToNext();
//            }
//        }
//
//        // get all cards isPractice
//        List<Card> cards = new ArrayList<>();
//        for (int i = 0; i < cardDecks.size(); i++) {
//            if (cardDecks.get(i).isPractice())
//                cards.add(getCardFromId(cardDecks.get(i).getCardId()));
//        }
//
//        cursor.close();
//        return cards;
//
//    }

//    public List<Profile> getProfiles() {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableProfile.TABLE_NAME, null);
//
//        // get all card-deck
//        List<Profile> profiles = new ArrayList<>();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//                profiles.add(fetchProfileFromCursor(cursor));
//                cursor.moveToNext();
//            }
//        }
//
//        cursor.close();
//        return profiles;
//    }

//    public Profile getProfileFromId(String profileId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableProfile.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);
//        Profile profile = null;
//
//        if (cursor.moveToFirst()) {
//            profile = (fetchProfileFromCursor(cursor));
//            cursor.moveToNext();
//        }
//        cursor.close();
//        return profile;
//    }

    ////////////// DELETE //////////////

//    public boolean deleteDeck(String deckId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//        List<Card> cards = getCardsFromDeck(deckId);
//        // delete card-deck
//        for (int counter = 0; counter < cards.size(); counter++) {
//            deleteCardDeck(cards.get(counter).getCardId(), deckId);
//        }
//
//        // resets the positions when deleting a deck
//        List<Deck> deckList = getDecks();
//        int deckToDeletePosition = getDeckFromId(deckId).getPosition();
//        ContentValues values = new ContentValues();
//
//        for (int i = 0; i < deckList.size(); i++) {
//            if (deckList.get(i).getPosition() > deckToDeletePosition) {
//                values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deckList.get(i).getPosition() - 1);
//                db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
//                        DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckList.get(i).getDeckId(), null);
//            }
//        }
//
//
//        return db.delete(DatabaseVariables.TableDeck.TABLE_NAME,
//                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null) > 0;
//    }


//    public boolean deleteCard(String cardId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//
//        List<Deck> decks = getDecksFromCard(cardId);
//        // delete card-deck
//        for (int counter = 0; counter < decks.size(); counter++) {
//            deleteCardDeck(cardId, decks.get(counter).getDeckId());
//        }
//
//        return db.delete(DatabaseVariables.TableCard.TABLE_NAME, DatabaseVariables.TableCard.COLUMN_NAME_CARD_ID
//                + "=" + cardId, null) > 0;
//    }

//    public boolean deleteCardDeck(String cardId, String deckId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//        return db.delete(DatabaseVariables.TableCardDeck.TABLE_NAME,
//                DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId + " AND "
//                        + DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null) > 0;
//    }

//    public boolean deleteProfile(String profileId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//        List<Card> cardList = getCards();
//        List<Deck> deckList = getDecks();
//
//        for (int c = 0; c < cardList.size(); c++)
//            deleteCard(cardList.get(c).getCardId());
//
//        for (int d = 0; d < cardList.size(); d++)
//            deleteDeck(deckList.get(d).getDeckId());
//
//
//        return db.delete(DatabaseVariables.TableProfile.TABLE_NAME, DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID
//                + "=" + profileId, null) > 0;
//    }

    ////////////// ARCHIVED //////////////

//
//
//    public List<Card> getArchivedCards() {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//        String activeProfileId = getActiveProfile().getProfileId();
//
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseVariables.TableCard.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableCard.COLUMN_NAME_ARCHIVED + "=" + 1 + " AND "
//                + DatabaseVariables.TableCard.COLUMN_NAME_PROFILE_ID + "=" + activeProfileId, null);
//        List<Card> cardList = new ArrayList<Card>();
//
//        if (cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//                cardList.add(fetchCardFromCursor(cursor));
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//
//        cardList = SortingStateManager.getInstance().sortCardList(cardList);
//
//        return cardList;
//    }


    ////////////// CREATE //////////////

//    public String createDeck(String name) {
//        SQLiteDatabase db = database.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        String date = getDateNow();
//
//        String activeProfileId = getActiveProfile().getProfileId();
//        int position = getDecks().size() + 1;
//
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME, name);
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_CREATED, date);
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED, date);
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_PROFILE_ID, activeProfileId);
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, position);
//
//
//        long newRowId = -1;
//        newRowId = db.insert(
//                DatabaseVariables.TableDeck.TABLE_NAME,
//                null,
//                values);
//        return Long.toString(newRowId);
//    }



//    public String createCardDeck(String cardId, String deckId) {
//        SQLiteDatabase db = database.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        String date = getDateNow();
//
//        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID, deckId);
//        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID, cardId);
//        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_DATE_ADDED, date);
//        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_IS_PRACTICE, 1);
//        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_DATE_TOGGLE_PRACTICE, "");
//
//        long newRowId = -1;
//        newRowId = db.insert(
//                DatabaseVariables.TableCardDeck.TABLE_NAME,
//                null,
//                values);
//        return Long.toString(newRowId);
//    }


//    public String createProfile(String name) {
//        SQLiteDatabase db = database.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        String date = getDateNow();
//
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_NAME, name);
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_CREATED, date);
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_QUESTION_LABEL, "Question");
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ANSWER_LABEL, "Answer");
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ACTIVE_QUIZ_ID, "");
//
//        long newRowId = -1;
//        newRowId = db.insert(
//                DatabaseVariables.TableProfile.TABLE_NAME,
//                null,
//                values);
//
//        // set newly created profile to active
//        setActiveProfile(Long.toString(newRowId));
//
//        return Long.toString(newRowId);
//    }

//    public String createUser() {
//
//        if (getUser() == null) {
//            SQLiteDatabase db = database.getWritableDatabase();
//            ContentValues values = new ContentValues();
//
//            String date = getDateNow();
//
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_DATE_CREATED, date);
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_ACTIVE_PROFILE_ID, "-1");
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_DEFAULT_SORTING, "0");
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_PROFILE_DELETION, 0);
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_DISPLAY_ALL_CARDS, 1);
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_DISPLAY_SPECIFIC_DECK, 0);
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_PRACTICE_ALL, 0);
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED, 1);
//            values.put(DatabaseVariables.TableUser.COLUMN_NAME_QUICK_TOGGLE_REVIEW, 12);
//
//
//            long newRowId = -1;
//            newRowId = db.insert(
//                    DatabaseVariables.TableUser.TABLE_NAME,
//                    null,
//                    values);
//
//            // set newly created profile to active
//            setActiveProfile(Long.toString(newRowId));
//
//            return Long.toString(newRowId);
//        } else {
//            return "-1";
//        }
//
//    }

    ////////////// UPDATE //////////////

//    public void updateDeck(String deckId, String name) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        String date = getDateNow();
//
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME, name);
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED, date);
//        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
//                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
//
//    }



//    public void updateProfile(String profileId, String name) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        String date = getDateNow();
//
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_NAME, name);
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
//        db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
//                DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);
//
//    }

//    public void updateProfileQuestionLabel(String profileId, String questionLabel) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        String date = getDateNow();
//
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_QUESTION_LABEL, questionLabel);
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
//        db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
//                DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);
//    }

//    public void updateProfileAnswerLabel(String profileId, String answerLabel) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        String date = getDateNow();
//
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_ANSWER_LABEL, answerLabel);
//        values.put(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_MODIFIED, date);
//        db.update(DatabaseVariables.TableProfile.TABLE_NAME, values,
//                DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID + "=" + profileId, null);
//    }

//    public void updateDefaultSortPosition(int position) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        User user = getUser();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_DEFAULT_SORTING, position);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//    }
//
//    public void updateQuickToggleReviewHours(int hours) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        User user = getUser();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_QUICK_TOGGLE_REVIEW, hours);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//    }


    ////////////// OTHER //////////////

//    public void togglePractice_Card(String cardId, String deckId, int time) {
//
//        CardDeck cardDeck = getCardDeck(cardId, deckId);
//
//        String dateToToggle = "";
//        if (time != -1) {
//            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//            Date now = Calendar.getInstance().getTime();
//
//            Date newDate = DateUtils.addHours(now, time);
//            dateToToggle = df.format(newDate);
//        }
//
//        Log.d("Database", "Toggle practice: " + dateToToggle);
//
//        boolean isPracticeCurrent = cardDeck.isPractice();
//        boolean newIsPractice = !isPracticeCurrent;
//
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_IS_PRACTICE, newIsPractice);
//        values.put(DatabaseVariables.TableCardDeck.COLUMN_NAME_DATE_TOGGLE_PRACTICE, dateToToggle);
//        db.update(DatabaseVariables.TableCardDeck.TABLE_NAME, values, DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID
//                + "=" + cardId + " AND " + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
//
//    }

//    public void setActiveProfile(String profileId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        User user = getUser();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_ACTIVE_PROFILE_ID, profileId);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//
//    }
//
//    public void swapDeckPosition(Deck deck1, Deck deck2) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        int deck1Position = deck1.getPosition();
//        int deck2Position = deck2.getPosition();
//
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deck2Position);
//        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
//                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deck1.getDeckId(), null);
//
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deck1Position);
//        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
//                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deck2.getDeckId(), null);
//
//    }


//    public void toggleAllowProfileDeletion() {
//
//        User user = getUser();
//
//        boolean currentIsProfileDeletion = user.isAllowProfileDeletion();
//        boolean newIsProfileDeletion = !currentIsProfileDeletion;
//
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_PROFILE_DELETION, newIsProfileDeletion);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//
//    }

//    public void toggleAllowSearchOnQueryChanged() {
//
//        User user = getUser();
//
//        boolean current = user.isAllowOnQueryChanged();
//        boolean next = !current;
//
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED, next);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//
//    }

//    public void toggleAllowPracticeAll() {
//
//        User user = getUser();
//
//        boolean current = user.isAllowPracticeAll();
//        boolean next = !current;
//
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_PRACTICE_ALL, next);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//
//    }
//
//    public void toggleDisplayNumberDecksAll() {
//
//        User user = getUser();
//
//        boolean currentDisplay = user.isDisplayNbDecksAllCards();
//        boolean newDisplay = !currentDisplay;
//
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_DISPLAY_ALL_CARDS, newDisplay);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//
//    }
//
//    public void toggleDisplayNumberDecksSpecific() {
//
//        User user = getUser();
//
//        boolean currentDisplay = user.isDisplayNbDecksSpecificCards();
//        boolean newDisplay = !currentDisplay;
//
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseVariables.TableUser.COLUMN_NAME_DISPLAY_SPECIFIC_DECK, newDisplay);
//        db.update(DatabaseVariables.TableUser.TABLE_NAME, values,
//                DatabaseVariables.TableUser.COLUMN_NAME_USER_ID + "=" + user.getUserId(), null);
//
//    }


    ////////////// HELPERS //////////////

//
//    private Deck fetchDeckFromCursor(Cursor cursor) {
//        String id = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID));
//        String name = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DECK_NAME));
//        String dateCreated = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_CREATED));
//        String dateModified = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_DATE_MODIFIED));
//        int position = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION));
//
//        return new Deck(id, name, dateCreated, dateModified, position);
//    }


//    private CardDeck fetchCardDeckFromCursor(Cursor cursor) {

//    }

//    private Profile fetchProfileFromCursor(Cursor cursor) {
//        String profileId = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_ID));
//        String profileName = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_PROFILE_NAME));
//        String dateAdded = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_DATE_CREATED));
//        String questionLabel = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_QUESTION_LABEL));
//        String answerLabel = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_ANSWER_LABEL));
//        String activeQuizId = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableProfile.COLUMN_NAME_ACTIVE_QUIZ_ID));
//
//
//        return new Profile(profileId, profileName, dateAdded, questionLabel, answerLabel, activeQuizId);
//    }


//    private User fetchUserFromCursor(Cursor cursor) {
//
//        String userId = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_USER_ID));
//        String activeProfileId = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_ACTIVE_PROFILE_ID));
//        String dateCreated = cursor.getString(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_DATE_CREATED));
//        int defaultSorting = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_DEFAULT_SORTING));
//        boolean allowProfileDeletion = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_PROFILE_DELETION)) > 0;
//        boolean displayAllCards = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_DISPLAY_ALL_CARDS)) > 0;
//        boolean displaySpecificDeck = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_DISPLAY_SPECIFIC_DECK)) > 0;
//        boolean allowPracticeAll = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_PRACTICE_ALL)) > 0;
//        boolean allowOnQueryChanged = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED)) > 0;
//        int quickToggle = cursor.getInt(cursor
//                .getColumnIndex(DatabaseVariables.TableUser.COLUMN_NAME_QUICK_TOGGLE_REVIEW));
//
//
//        return new User(userId, dateCreated, activeProfileId, defaultSorting, allowProfileDeletion, displayAllCards, displaySpecificDeck, allowPracticeAll, allowOnQueryChanged, quickToggle);
//    }

//    public Profile getActiveProfile() {
//
//        User user = getUser();
//        Profile profile = getProfileFromId(user.getActiveProfileId());
//
//        return profile;
//    }

//    public void changeDeckPosition(int newPosition, Deck deck) {
//
//
//        SQLiteDatabase db = database.getReadableDatabase();
//        ContentValues values = new ContentValues();
//
//        List<Deck> deckList = getDecks();
//
//
//        values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, newPosition);
//        db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
//                DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deck.getDeckId(), null);
//
//        for (int i = 0; i < deckList.size(); i++) {
//            int deckTempPosition = deckList.get(i).getPosition();
//            if (deck.getPosition() < newPosition) {
//                if (deckTempPosition > deck.getPosition() && deckTempPosition <= newPosition) {
//                    values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deckTempPosition - 1);
//                    db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
//                            DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckList.get(i).getDeckId(), null);
//                }
//            } else {
//                if (deckTempPosition < deck.getPosition() && deckTempPosition >= newPosition) {
//                    values.put(DatabaseVariables.TableDeck.COLUMN_NAME_POSITION, deckTempPosition + 1);
//                    db.update(DatabaseVariables.TableDeck.TABLE_NAME, values,
//                            DatabaseVariables.TableDeck.COLUMN_NAME_DECK_ID + "=" + deckList.get(i).getDeckId(), null);
//                }
//            }
//        }
//
//    }

//    public int numberCardsInDeck(String deckId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//
//        int numRows = (int) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableCardDeck.COLUMN_NAME_DECK_ID + "=" + deckId, null);
//        return numRows;
//    }
//
//    public int numberDecksInCard(String cardId) {
//        SQLiteDatabase db = database.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM " + DatabaseVariables.TableCardDeck.TABLE_NAME + " WHERE "
//                + DatabaseVariables.TableCardDeck.COLUMN_NAME_CARD_ID + "=" + cardId, null);
//        return numRows;
//    }

}
