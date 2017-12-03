package stelztech.youknowehv4.database.card;

import java.util.List;

/**
 * Created by alex on 10/14/2017.
 */

public interface ICardDao {

    List<Card> fetchAllCards();
    Card fetchCardById(int cardId);
    int createCard(String question, String answer, String moreInfo);
    int createCard(String question, String answer, String moreInfo, String dateCreate, String dateModified);
    boolean updateCard(int cardId, String question, String answer, String moreInfo);
    boolean deleteCard(int cardId);
    boolean toggleArchiveCard(int cardId);
    List<Card> fetchArchivedCards(); // for current profile
    int fetchNumberOfCardsByProfileId(int profileId);
}
