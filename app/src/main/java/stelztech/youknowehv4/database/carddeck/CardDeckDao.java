package stelztech.youknowehv4.database.carddeck;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.List;

import stelztech.youknowehv4.database.DbContentProvider;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class CardDeckDao extends DbContentProvider implements ICardDeckDao, ICardDeckSchema {


    public CardDeckDao(SQLiteDatabase db) {
        super(db);
    }


    @Override
    public CardDeck fetchCardDeckById(long cardId, long deckId) {
        return null;
    }

    @Override
    public List<Card> fetchCardsByDeckId(long deckId) {
        return null;
    }

    @Override
    public List<Deck> fetchDecksByCardId(long cardId) {
        return null;
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
