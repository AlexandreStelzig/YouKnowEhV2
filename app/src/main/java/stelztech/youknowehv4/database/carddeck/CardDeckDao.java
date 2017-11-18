package stelztech.youknowehv4.database.carddeck;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.helper.DateHelper;
import stelztech.youknowehv4.manager.SortingStateManager;

import static stelztech.youknowehv4.database.carddeck.CardDeck.REVIEW_TOGGLE_ID;


/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeckDao extends DbContentProvider implements ICardDeckDao, ICardDeckSchema {

    private Cursor cursor;

    public CardDeckDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public CardDeck fetchCardDeckById(int cardId, int deckId) {

        cursor = super.rawQuery("SELECT * FROM " + CARD_DECK_TABLE + " WHERE " +
                COLUMN_CARD_ID + "=" + cardId + " AND " + COLUMN_DECK_ID + "=" + deckId, null);

        CardDeck cardDeck = null;

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                cardDeck = cursorToEntity(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return cardDeck;
    }

    @Override
    public List<Card> fetchCardsByDeckId(int deckId) {

        cursor = super.rawQuery("SELECT * FROM " + CARD_DECK_TABLE + " WHERE " + COLUMN_DECK_ID + "=" + deckId, null);

        // get all card-deck
        List<CardDeck> cardDecks = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    cardDecks.add(cursorToEntity(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        // get all cards
        List<Card> cardList = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            cardList.add(Database.mCardDao.fetchCardById(cardDecks.get(i).getCardId()));
        }

        cardList = SortingStateManager.getInstance().sortCardList(cardList);

        return cardList;
    }

    @Override
    public List<Deck> fetchDecksByCardId(int cardId) {
        cursor = super.rawQuery("SELECT * FROM " + CARD_DECK_TABLE + " WHERE " + COLUMN_CARD_ID + "=" + cardId, null);

        // get all card-deck
        List<CardDeck> cardDecks = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    cardDecks.add(cursorToEntity(cursor));
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }

        // get all decks
        List<Deck> deckList = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            deckList.add(Database.mDeckDao.fetchDeckById(cardDecks.get(i).getDeckId()));
        }

        deckList = SortingStateManager.getInstance().sortDeck(deckList);

        return deckList;
    }

    @Override
    public boolean revalidateReviewCards() {
        // todo only fetch those that are not review
        cursor = super.rawQuery("SELECT * FROM " + CARD_DECK_TABLE, null);


        List<CardDeck> cardDecks = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cardDecks.add(cursorToEntity(cursor));
                cursor.moveToNext();
            }
        }

        List<CardDeck> cardDecksNotPractice = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            if (!cardDecks.get(i).isPractice())
                cardDecksNotPractice.add(cardDecks.get(i));
        }

        cardDecks.clear();

        Date dateNow = DateHelper.getDateNow();


        for (int counter = 0; counter < cardDecksNotPractice.size(); counter++) {

            CardDeck temp = cardDecksNotPractice.get(counter);
            String dateToggle = temp.getPracticeToggleDate();

            if (!dateToggle.equals("")) {

                Date date = DateHelper.stringToDate(dateToggle);
                boolean readyToToggle = dateNow.after(date);

                if (readyToToggle) {
                    boolean passed = changeCardReviewTime(temp.getCardId(), temp.getDeckId(), REVIEW_TOGGLE_ID);

                    if(!passed)
                        return false;
                }

            }
        }

        return true;
    }

    @Override
    public List<Card> fetchReviewCardsByDeckId(int deckId) {

        cursor = super.rawQuery("SELECT * FROM " + CARD_DECK_TABLE + " WHERE "
                + COLUMN_DECK_ID + "=" + deckId, null);

        List<CardDeck> cardDecks = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cardDecks.add(cursorToEntity(cursor));
                cursor.moveToNext();
            }
        }

        // get all cards isPractice
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            if (cardDecks.get(i).isPractice())
                cards.add(Database.mCardDao.fetchCardById(cardDecks.get(i).getCardId()));
        }

        cursor.close();
        return cards;
    }

    @Override
    public boolean deleteCardDeck(int cardId, int deckId) {

        return super.delete(CARD_DECK_TABLE, COLUMN_CARD_ID + "=" + cardId + " AND " + COLUMN_DECK_ID + "=" + deckId, null) > 0;
    }

    @Override
    public long createCardDeck(int cardId, int deckId) {
        ContentValues values = new ContentValues();


        String date = DateHelper.getDateNowString();

        values.put(COLUMN_DECK_ID, deckId);
        values.put(COLUMN_CARD_ID, cardId);
        values.put(COLUMN_DATE_CREATED, date);
        values.put(COLUMN_IS_REVIEW, 1);
        values.put(COLUMN_DATE_TOGGLE_REVIEW, "");

        try {
            return super.insert(CARD_DECK_TABLE, values);
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return -1;
        }
    }

    @Override
    public boolean changeCardReviewTime(int cardId, int deckId, int nbHoursToBeToggled) {
        ContentValues values = new ContentValues();

        CardDeck cardDeck = fetchCardDeckById(cardId, deckId);

        String dateToToggle = "";
        if (nbHoursToBeToggled != REVIEW_TOGGLE_ID) {
            // todo change to date helper
            DateFormat df = new SimpleDateFormat(DateHelper.DEFAULT_DATE_FORMAT);
            Date now = DateHelper.getDateNow();

            Date newDate = DateUtils.addHours(now, nbHoursToBeToggled);
            dateToToggle = df.format(newDate);
        }

        try {
            Log.d("Database", "Toggle practice: " + dateToToggle);

            boolean isPracticeCurrent = cardDeck.isPractice();
            boolean newIsPractice = !isPracticeCurrent;

            values.put(COLUMN_IS_REVIEW, newIsPractice);
            values.put(COLUMN_DATE_TOGGLE_REVIEW, dateToToggle);

            return super.update(CARD_DECK_TABLE, values, COLUMN_CARD_ID + "=" + cardId + " AND " + COLUMN_DECK_ID + "=" + deckId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    @Override
    public int fetchNumberCardsFromDeckId(int deckId) {
        return (int) DatabaseUtils.longForQuery(mDb, "SELECT COUNT(*) FROM " + CARD_DECK_TABLE + " WHERE "
                + COLUMN_DECK_ID + "=" + deckId, null);
    }

    @Override
    public int fetchNumberDecksFromCardId(int cardId) {
        return (int) DatabaseUtils.longForQuery(mDb, "SELECT COUNT(*) FROM " + CARD_DECK_TABLE + " WHERE "
                + COLUMN_CARD_ID + "=" + cardId, null);
    }

    @Override
    protected CardDeck cursorToEntity(Cursor cursor) {
        int deckId = cursor.getInt(cursor.getColumnIndex(COLUMN_DECK_ID));
        int cardId = cursor.getInt(cursor.getColumnIndex(COLUMN_CARD_ID));
        boolean isPractice = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_REVIEW)) > 0;
        String dateCreated = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_CREATED));
        String dateTogglePractice = cursor.getString(cursor.getColumnIndex(COLUMN_DATE_TOGGLE_REVIEW));


        return new CardDeck(deckId, cardId, isPractice, dateCreated, dateTogglePractice);
    }

}
