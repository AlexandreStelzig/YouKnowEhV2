package stelztech.youknowehv4.database.carddeck;

import java.util.List;

import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;

/**
 * Created by alex on 10/14/2017.
 */

public interface ICardDeckDao {

    CardDeck fetchCardDeckById(int cardId, int deckId);
    List<Card> fetchCardsByDeckId(int deckId);
    List<Deck> fetchDecksByCardId(int cardId);
    boolean revalidateReviewCards();
    List<Card> fetchReviewCardsByDeckId(int deckId);
    boolean deleteCardDeck(int cardId, int deckId);
    long createCardDeck(int cardId, int deckId);
    boolean changeCardReviewTime(int cardId, int deckId, int hours);
    int fetchNumberCardsFromDeckId(int deckId);
    int fetchNumberDecksFromCardId(int cardId);
    boolean setReviewToggleDate(int cardId, int deckId, String date);

}
