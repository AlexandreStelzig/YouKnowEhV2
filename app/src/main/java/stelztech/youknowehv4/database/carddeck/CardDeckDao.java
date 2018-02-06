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
import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.utilities.DateUtilities;
import stelztech.youknowehv4.manager.SortingStateManager;

import static stelztech.youknowehv4.database.card.ICardSchema.CARD_TABLE;
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

    public int fetchNumberOfCardsCreatedOnDateByDeckId(int deckId, String date) {
        return (int) DatabaseUtils.longForQuery(mDb, "SELECT COUNT(*) FROM " + CARD_DECK_TABLE + " INNER JOIN " + ICardSchema.CARD_TABLE
                + " ON " + CARD_DECK_TABLE + "." + COLUMN_CARD_ID + "=" +ICardSchema.CARD_TABLE+"."+ ICardSchema.COLUMN_CARD_ID
                + " WHERE " + CARD_DECK_TABLE + "."
                + COLUMN_DECK_ID + "=" + deckId + " AND "
                + ICardSchema.CARD_TABLE + "." + ICardSchema.COLUMN_DATE_CREATED + " LIKE '%" + date +"%'", null);
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
        cursor = super.rawQuery("SELECT * FROM " + CARD_DECK_TABLE
                + " WHERE " + COLUMN_IS_REVIEW + "=" + 0, null);

        return revalidateReviewForCardList(cursor);
    }

    public boolean revalidateReviewCardsByDeckId(int deckId) {
        cursor = super.rawQuery("SELECT * FROM " + CARD_DECK_TABLE
                + " WHERE " + COLUMN_IS_REVIEW + "=" + 0
                + " AND " + COLUMN_DECK_ID + "=" + deckId, null);

        return revalidateReviewForCardList(cursor);
    }

    private boolean revalidateReviewForCardList(Cursor cursor){

        List<CardDeck> cardDecks = new ArrayList<>();

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                cardDecks.add(cursorToEntity(cursor));
                cursor.moveToNext();
            }
        }

        Date dateNow = DateUtilities.getDateNow();


        for (int counter = 0; counter < cardDecks.size(); counter++) {

            CardDeck temp = cardDecks.get(counter);
            String dateToggle = temp.getReviewToggleDate();

            if (!dateToggle.equals("")) {

                Date date = DateUtilities.stringToDate(dateToggle);
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

        // get all cards isReview
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < cardDecks.size(); i++) {
            if (cardDecks.get(i).isReview())
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
    public int createCardDeck(int cardId, int deckId) {
        ContentValues values = new ContentValues();


        String date = DateUtilities.getDateNowString();

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
            DateFormat df = new SimpleDateFormat(DateUtilities.DEFAULT_DATE_FORMAT);
            Date now = DateUtilities.getDateNow();

            Date newDate = DateUtils.addHours(now, nbHoursToBeToggled);
            dateToToggle = df.format(newDate);
        }

        try {
            Log.d("Database", "Toggle practice: " + dateToToggle);

            boolean isPracticeCurrent = cardDeck.isReview();
            boolean newIsPractice = !isPracticeCurrent;

            values.put(COLUMN_IS_REVIEW, newIsPractice);
            values.put(COLUMN_DATE_TOGGLE_REVIEW, dateToToggle);

            return super.update(CARD_DECK_TABLE, values, COLUMN_CARD_ID + "=" + cardId + " AND " + COLUMN_DECK_ID + "=" + deckId, null) > 0;
        } catch (SQLiteConstraintException ex) {
            Log.w("Database", ex.getMessage());
            return false;
        }
    }

    public boolean setReviewToggleDate(int cardId, int deckId, String date){
        ContentValues values = new ContentValues();


        try {
            Log.d("Database", "Toggle practice: " + date);

            boolean newIsPractice = false;

            values.put(COLUMN_IS_REVIEW, newIsPractice);
            values.put(COLUMN_DATE_TOGGLE_REVIEW, date);

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
    public int fetchNumberReviewCardsFromDeck(int deckId) {
        return (int) DatabaseUtils.longForQuery(mDb, "SELECT COUNT(*) FROM " + CARD_DECK_TABLE + " WHERE "
                + COLUMN_DECK_ID + "=" + deckId + " AND " + COLUMN_IS_REVIEW + "=" + 1, null);
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
