package stelztech.youknowehv4.database.deck;

import java.util.List;

import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/14/2017.
 */

public interface IDeckDao {

    List<Deck> fetchAllDecks();
    Deck fetchDeckFromId(int deckId);
    boolean deleteDeck(int deckId);
    int createDeck(String deckName);
    boolean updateDeck(int deckId, String newDeckName);
    void swapDeckPosition(Deck deck1, Deck deck2);
    void changeDeckPosition(int newPosition, Deck deck);
}
