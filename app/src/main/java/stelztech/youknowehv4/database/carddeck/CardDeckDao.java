package stelztech.youknowehv4.database.carddeck;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.manager.SortingStateManager;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeckDao extends DbContentProvider implements ICardDeckDao, ICardDeckSchema {

    private Cursor cursor;

    public CardDeckDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public CardDeck fetchCardDeckById(long cardId, long deckId) {

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
    public List<Card> fetchCardsByDeckId(long deckId) {

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
    public List<Deck> fetchDecksByCardId(long cardId) {
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
        return false;
    }

    @Override
    public List<Card> fetchReviewCardsByDeckId(long deckId) {
        return null;
    }

    @Override
    public boolean deleteCardDeck(long cardId, long deckId) {
        return false;
    }

    @Override
    public long createCardDeck(long cardId, long deckId) {
        return 0;
    }

    @Override
    public boolean toggleCardFromReview(long cardId, long deckId, int hours) {
        return false;
    }

    @Override
    public int fetchNumberCardsForDeck(long deckId) {
        return 0;
    }

    @Override
    public int fetchNumberDecksForCar(long cardId) {
        return 0;
    }

    @Override
    protected CardDeck cursorToEntity(Cursor cursor) {
        Long deckId = cursor.getLong(cursor.getColumnIndex(COLUMN_DECK_ID));
        Long cardId = cursor.getLong(cursor.getColumnIndex(COLUMN_CARD_ID));
        boolean isPractice = cursor.getInt(cursor.getColumnIndex(COLUMN_IS_PRACTICE)) > 0;
        Date dateCreated = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_CREATED)));
        Date dateTogglePractice = new Date(cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_TOGGLE_PRACTICE)));


        return new CardDeck(deckId, cardId, isPractice, dateCreated, dateTogglePractice);
    }

}
