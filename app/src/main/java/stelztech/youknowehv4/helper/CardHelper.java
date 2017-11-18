package stelztech.youknowehv4.helper;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.deck.Deck;
import stelztech.youknowehv4.database.profile.Profile;

/**
 * Created by alex on 10/1/2017.
 */

public class CardHelper {



    public static void showQuickInfoCard(Context context, Activity activity, Card cardToDisplay) {

        Profile profile = Database.mUserDao.fetchActiveProfile();

        String questionLabel = profile.getQuestionLabel();
        String answerLabel = profile.getAnswerLabel();


        String cardQuestion = questionLabel + ": " + cardToDisplay.getQuestion();
        String cardAnswer = answerLabel + ": " + cardToDisplay.getAnswer();
        String cardComments = cardToDisplay.getMoreInfo();

        List<Deck> decks = Database.mCardDeckDao.fetchDecksByCardId(cardToDisplay.getCardId());
        String numberOfDecks = "Number of Decks: " + decks.size();
        String cardDateCreated = "Date created: " + cardToDisplay.getDateCreated();
        String cardDateModified = "Date modified: " + cardToDisplay.getDateModified();


        String cardCommentsText = "Comments:\n" + cardComments;

        String message = cardQuestion + "\n" + cardAnswer + "\n" + numberOfDecks + "\n"
                + cardDateCreated + "\n" + cardDateModified;

        if (!cardComments.isEmpty()) {
            message += "\n" + cardCommentsText;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setMessage(message).setPositiveButton("done", null).show();
    }
}
