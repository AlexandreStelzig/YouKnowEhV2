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
    int createCardDeck(int cardId, int deckId);
    boolean toggleCardFromReview(int cardId, int deckId, int hours);
    int fetchNumberCardsForDeck(int deckId);
    int fetchNumberDecksForCar(int cardId);

}