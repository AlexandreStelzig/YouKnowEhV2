package stelztech.youknowehv4.utilities;

import android.app.Activity;
import android.text.Html;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import stelztech.youknowehv4.components.CustomProgressDialog;
import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.carddeck.CardDeck;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/1/2017.
 */

public class CardUtilities {


    public static void showQuickInfoCard(Activity activity, Card cardToDisplay, Deck deckAssociated) {

        CardDeck cardDeck = null;
        if (deckAssociated != null)
            cardDeck = Database.mCardDeckDao.fetchCardDeckById(cardToDisplay.getCardId(), deckAssociated.getDeckId());

        Profile profile = Database.mUserDao.fetchActiveProfile();

        String questionLabel = profile.getFrontLabel();
        String answerLabel = profile.getBackLabel();


        String cardQuestion = "<b>" + questionLabel + ": </b>" + cardToDisplay.getQuestion();
        String cardAnswer = "<b>" + answerLabel + ": </b>" + cardToDisplay.getAnswer();
        String cardComments = cardToDisplay.getMoreInfo();

        List<Deck> decks = Database.mCardDeckDao.fetchDecksByCardId(cardToDisplay.getCardId());
        String numberOfDecks = "<b>Number of Decks: </b>" + decks.size();
        String cardDateCreated = "<b>Date created: </b>" + cardToDisplay.getDateCreated();
        String cardDateModified = "<b>Date modified: </b>" + cardToDisplay.getDateModified();


        String cardCommentsText = "<b>Comments: </b><br>" + cardComments;

        String message = cardQuestion + "<br>" + cardAnswer + "<br>" + numberOfDecks + "<br>"
                + cardDateCreated + "<br>" + cardDateModified;

        if (cardDeck != null && !cardDeck.isReview()) {
            String practiceToggleText = "<b>Card Review Toggle: </b>";
            if (!DateUtilities.isValidDate(cardDeck.getReviewToggleDate())) {
                practiceToggleText = practiceToggleText + "Until Toggled Manually";
            } else {
                Date dateNow = DateUtilities.getDateNow();
                Date dateToggle = DateUtilities.stringToDate(cardDeck.getReviewToggleDate());

                long timeDiff = dateToggle.getTime() - dateNow.getTime();

                TimeUnit timeUnitHours = TimeUnit.HOURS;
                long hoursUntilToggle = timeUnitHours.convert(timeDiff, TimeUnit.MILLISECONDS);

                TimeUnit timeUnitMinutes = TimeUnit.MINUTES;
                long minutesUntilToggle = timeUnitMinutes.convert(timeDiff, TimeUnit.MILLISECONDS);

                minutesUntilToggle = minutesUntilToggle - (hoursUntilToggle * 60);

                if (timeDiff > 0)
                    practiceToggleText = practiceToggleText + hoursUntilToggle + "h" + minutesUntilToggle;
                else
                    practiceToggleText = practiceToggleText + "On App Reload";
            }
            message += "<br>" + practiceToggleText;
        }

        if (!cardComments.isEmpty()) {
            message += "<br>" + cardCommentsText;
        }


        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle("Quick Info");
        builder.setMessage(Html.fromHtml(message)).setPositiveButton("done", null).show();
    }


    public static void mergeDuplicates(CustomProgressDialog customProgressDialog, int deckId) {
        if (customProgressDialog != null) {
            customProgressDialog.setDialogTitle("Merging Duplicates");
        }

        List<Card> allCards;
        if (deckId == -1) {
            if (customProgressDialog != null)
                customProgressDialog.setDialogMax(Database.mCardDao.fetchNumberOfCardsByProfileId(Database.mUserDao.fetchActiveProfile().getProfileId()));
            allCards = Database.mCardDao.fetchAllCards();

        } else {
            if (customProgressDialog != null)
                customProgressDialog.setDialogMax(Database.mCardDeckDao.fetchNumberCardsFromDeckId(deckId));
            allCards = Database.mCardDeckDao.fetchCardsByDeckId(deckId);
        }

        if (customProgressDialog != null) {
            customProgressDialog.setDialogProgress(0);
        }

        for (int counterOne = 0; counterOne < allCards.size(); counterOne++) {

            Card cardOne = allCards.get(counterOne);

            int counterTwo = counterOne + 1;
            while (counterTwo < allCards.size()) {

                Card cardTwo = allCards.get(counterTwo);

                if (Objects.equals(cardOne.getQuestion(), cardTwo.getQuestion()) &&
                        Objects.equals(cardOne.getAnswer(), cardTwo.getAnswer()) &&
                        Objects.equals(cardOne.getMoreInfo(), cardTwo.getMoreInfo())) {

                    List<Deck> cardOneDecks = Database.mCardDeckDao.fetchDecksByCardId(cardOne.getCardId());
                    List<Deck> cardTwoDecks = Database.mCardDeckDao.fetchDecksByCardId(cardTwo.getCardId());

                    boolean isInSameDeck = false;

                    for (int cardOneCounter = 0; cardOneCounter < cardOneDecks.size(); cardOneCounter++) {
                        for (int cardTwoDecksCounter = 0; cardTwoDecksCounter < cardTwoDecks.size(); cardTwoDecksCounter++) {
                            if (cardOneDecks.get(cardOneCounter).getDeckId() == (cardTwoDecks.get(cardTwoDecksCounter).getDeckId())) {
                                isInSameDeck = true;
                                break;
                            }
                        }
                    }

                    if (!isInSameDeck) {
                        for (int i = 0; i < cardTwoDecks.size(); i++) {
                            Database.mCardDeckDao.createCardDeck(cardOne.getCardId(), cardTwoDecks.get(i).getDeckId());
                        }
                    }

                    Database.mCardDao.deleteCard(cardTwo.getCardId());
                    allCards.remove(counterTwo);

                } else {
                    counterTwo++;
                }


            }

            if (customProgressDialog != null) {
                customProgressDialog.setDialogMax(allCards.size());
                customProgressDialog.setDialogProgress(counterOne);
            }


        }
    }

}
