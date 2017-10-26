package stelztech.youknowehv4.database.card;

import java.util.List;

/**
 * Created by alex on 10/14/2017.
 */

public interface ICardDao {

    List<Card> fetchAllCards();
    Card fetchCardById(long cardId);
    long createCard(String question, String answer, String moreInfo);
    boolean updateCard(long cardId, String question, String answer, String moreInfo);
    boolean deleteCard(long cardId);
    boolean toggleArchiveCard(long cardId);
    List<Card> fetchArchivedCards(); // for current profile
}
