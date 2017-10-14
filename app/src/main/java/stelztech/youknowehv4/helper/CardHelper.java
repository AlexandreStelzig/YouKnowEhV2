package stelztech.youknowehv4.helper;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import stelztech.youknowehv4.database.DatabaseManager;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.model.Deck;
import stelztech.youknowehv4.model.Profile;

/**
 * Created by alex on 10/1/2017.
 */

public class CardHelper {



    public static void showQuickInfoCard(Context context, Activity activity, Card cardToDisplay) {

        DatabaseManager dbManager = DatabaseManager.getInstance(context);
        Profile profile = dbManager.getActiveProfile();

        String questionLabel = profile.getQuestionLabel();
        String answerLabel = profile.getAnswerLabel();


        String cardQuestion = questionLabel + ": " + cardToDisplay.getQuestion();
        String cardAnswer = answerLabel + ": " + cardToDisplay.getAnswer();
        String cardComments = cardToDisplay.getMoreInfo();

        List<Deck> decks = dbManager.getDecksFromCard(cardToDisplay.getCardId());
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
