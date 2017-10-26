package stelztech.youknowehv4.database.carddeck;

import java.util.List;

import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;

/**
 * Created by alex on 10/14/2017.
 */

public interface ICardDeckDao {

    CardDeck fetchCardDeckById(long cardId, long deckId);
    List<Card> fetchCardsByDeckId(long deckId);
    List<Deck> fetchDecksByCardId(long cardId);
    boolean revalidateReviewCards();
    List<Card> fetchReviewCardsByDeckId(long deckId);
    boolean deleteCardDeck(long cardId, long deckId);
    long createCardDeck(long cardId, long deckId);
    boolean toggleCardFromReview(long cardId, long deckId, int hours);
    int fetchNumberCardsForDeck(long deckId);
    int fetchNumberDecksForCar(long cardId);

}
