package stelztech.youknowehv4.helper;

import android.app.Activity;
import android.content.Context;
import android.text.Html;

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


        String cardQuestion = "<b>"+ questionLabel + ": </b>" + cardToDisplay.getQuestion();
        String cardAnswer = "<b>"+answerLabel + ": </b>" + cardToDisplay.getAnswer();
        String cardComments = cardToDisplay.getMoreInfo();

        List<Deck> decks = Database.mCardDeckDao.fetchDecksByCardId(cardToDisplay.getCardId());
        String numberOfDecks = "<b>Number of Decks: </b>" + decks.size();
        String cardDateCreated = "<b>Date created: </b>" + cardToDisplay.getDateCreated();
        String cardDateModified = "<b>Date modified: </b>" + cardToDisplay.getDateModified();


        String cardCommentsText = "<b>Comments: </b><br>" + cardComments;

        String message = cardQuestion + "<br>" + cardAnswer + "<br>" + numberOfDecks + "<br>"
                + cardDateCreated + "<br>" + cardDateModified;

        if (!cardComments.isEmpty()) {
            message += "<br>" + cardCommentsText;
        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(activity);
        builder.setTitle("Quick Info");
        builder.setMessage(Html.fromHtml(message)).setPositiveButton("done", null).show();
    }
}
