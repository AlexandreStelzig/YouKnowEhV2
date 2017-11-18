package stelztech.youknowehv4.database.deck;

import java.util.List;

/**
 * Created by alex on 10/14/2017.
 */

public interface IDeckDao {

    List<Deck> fetchAllDecks();
    Deck fetchDeckById(int deckId);
    boolean deleteDeck(int deckId);
    int createDeck(String deckName);
    boolean updateDeck(int deckId, String newDeckName);
    boolean swapDeckPosition(Deck deck1, Deck deck2);
    boolean changeDeckPosition(int newPosition, Deck deck);
}
