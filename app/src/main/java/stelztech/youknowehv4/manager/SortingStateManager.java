package stelztech.youknowehv4.manager;

import android.content.Context;

import java.text.Collator;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import stelztech.youknowehv4.database.Database;
import stelztech.youknowehv4.database.card.Card;
import stelztech.youknowehv4.database.carddeck.CardDeck;
import stelztech.youknowehv4.database.deck.Deck;

/**
 * Created by alex on 2017-05-06.
 */

public class SortingStateManager {

    public enum SortingStates {
        AZ_QUESTION,
        ZA_QUESTION,
        AZ_ANSWER,
        ZA_ANSWER,
        DATE_CREATED_NEW_OLD,
        DATE_CREATED_OLD_NEW,
        DATE_MODIFIED_NEW_OLD,
        DATE_MODIFIED_OLD_NEW,
        NB_DECK
    }

    private SortingStates currentState;

    private Context context;

    private static SortingStateManager instance;

    private SortingStateManager() {

        changeStateByPosition(getDefaultSort());

    }

    public void setContext(Context context){
        this.context = context;
    }

    public static SortingStateManager getInstance() {
        if (instance == null)
            instance = new SortingStateManager();
        return instance;
    }


    public void changeSate(SortingStates state) {
        currentState = state;
    }

    public List<Card> sortCardList(List<Card> cardList) {

        List<Card> cardListSorted = cardList;
        switch (currentState) {

            case AZ_QUESTION:
                cardListSorted = sortAlphabetically_Card_Question(cardList);
                break;
            case ZA_QUESTION:
                cardListSorted = sortReverseAlphabetically_Card_Question(cardList);
                break;
            case AZ_ANSWER:
                cardListSorted = sortAlphabetically_Card_Answer(cardList);
                break;
            case ZA_ANSWER:
                cardListSorted = sortReverseAlphabetically_Card_Answer(cardList);
                break;
            case DATE_CREATED_NEW_OLD:
                cardListSorted = sortByDateCreated_NEW_OLD(cardList);
                break;
            case DATE_CREATED_OLD_NEW:
                cardListSorted = sortByDateCreated_OLD_NEW(cardList);
                break;
            case DATE_MODIFIED_NEW_OLD:
                cardListSorted = sortByDateModified_NEW_OLD(cardList);
                break;
            case DATE_MODIFIED_OLD_NEW:
                cardListSorted = sortByDateModified_OLD_NEW(cardList);
                break;
            case NB_DECK:
                cardListSorted = sortByDeck(cardList);
                break;
        }
        return cardListSorted;
    }


    private List<Card> sortAlphabetically_Card_Question(List<Card> list) {

        Collections.sort(list, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {


                Collator usCollator = Collator.getInstance(Locale.US);
                usCollator.setStrength(Collator.PRIMARY);

                String lowerCase1 = o1.getQuestion().toLowerCase();
                String lowerCase2 = o2.getQuestion().toLowerCase();

                String o1String = Normalizer.normalize(lowerCase1, Normalizer.Form.NFD);
                String o2String = Normalizer.normalize(lowerCase2, Normalizer.Form.NFD);

                String o1StringPart = o1String.replaceAll("\\d", "");
                String o2StringPart = o2String.replaceAll("\\d", "");


                if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
                    return extractInt(o1String) - extractInt(o2String);
                }
                return o1String.compareTo(o2String);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        return list;

    }

    private List<Card> sortAlphabetically_Card_Answer(List<Card> list) {

        Collections.sort(list, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {

                String lowerCase1 = o1.getAnswer().toLowerCase();
                String lowerCase2 = o2.getAnswer().toLowerCase();

                String o1String = Normalizer.normalize(lowerCase1, Normalizer.Form.NFD);
                String o2String = Normalizer.normalize(lowerCase2, Normalizer.Form.NFD);

                String o1StringPart = o1String.replaceAll("\\d", "");
                String o2StringPart = o2String.replaceAll("\\d", "");


                if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
                    return extractInt(o1String) - extractInt(o2String);
                }
                return o1String.compareTo(o2String);
            }

            int extractInt(String s) {
                String num = s.replaceAll("\\D", "");
                // return 0 if no digits found
                return num.isEmpty() ? 0 : Integer.parseInt(num);
            }
        });
        return list;

    }

    private List<Card> sortReverseAlphabetically_Card_Question(List<Card> list) {
        List<Card> tempList = sortAlphabetically_Card_Question(list);
        Collections.reverse(tempList);
        return list;

    }

    private List<Card> sortReverseAlphabetically_Card_Answer(List<Card> list) {
        List<Card> tempList = sortAlphabetically_Card_Answer(list);
        Collections.reverse(tempList);
        return tempList;
    }


    private List<Card> sortByDateCreated_NEW_OLD(List<Card> list) {
        Collections.sort(list, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {


                String o1Date = o1.getDateCreated().toString();
                String o2Date = o2.getDateCreated().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


                try {

                    return sdf.parse(o2Date).compareTo(sdf.parse(o1Date));

                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return list;
    }

    private List<Card> sortByDateModified_NEW_OLD(List<Card> list) {
        Collections.sort(list, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {


                String o1Date = o1.getDateModified().toString();
                String o2Date = o2.getDateModified().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


                try {

                    return sdf.parse(o2Date).compareTo(sdf.parse(o1Date));

                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        return list;
    }

    private List<Card> sortByDateCreated_OLD_NEW(List<Card> list) {
        List<Card> tempList = sortByDateCreated_NEW_OLD(list);
        Collections.reverse(tempList);
        return list;

    }

    private List<Card> sortByDateModified_OLD_NEW(List<Card> list) {
        List<Card> tempList = sortByDateModified_NEW_OLD(list);
        Collections.reverse(tempList);
        return list;

    }

    public List<Card> sortByPractice(List<Card> cardList, int deckId) {


        List<Card> nonPracticeCards = new ArrayList<>();
        List<Card> practiceCards = new ArrayList<>();
        for (int counter = 0; counter < cardList.size(); counter++) {
            Card card = cardList.get(counter);
            CardDeck cardDeck = Database.mCardDeckDao.fetchCardDeckById(card.getCardId(), deckId);

            if (cardDeck.isPractice())
                practiceCards.add(card);
            else
                nonPracticeCards.add(card);
        }

        cardList.clear();
        cardList.addAll(practiceCards);
        cardList.addAll(nonPracticeCards);


        return cardList;
    }

    private List<Card> sortByDeck(List<Card> cardList) {

        Collections.sort(cardList, new Comparator<Card>() {
            @Override
            public int compare(Card o1, Card o2) {

                int position1 = Database.mCardDeckDao.fetchNumberDecksFromCardId(o1.getCardId());
                int position2 = Database.mCardDeckDao.fetchNumberDecksFromCardId(o2.getCardId());


                return position1 - position2;
            }
        });


        return cardList;
    }

    public List<Deck> sortDeck(List<Deck> list) {

        Collections.sort(list, new Comparator<Deck>() {
            @Override
            public int compare(Deck o1, Deck o2) {

                int position1 = o1.getPosition();
                int position2 = o2.getPosition();


                return position1 - position2;
            }
        });
        return list;

    }

    public int getSelectedPosition() {
        switch (currentState) {
            case AZ_QUESTION:
                return 0;
            case ZA_QUESTION:
                return 1;
            case AZ_ANSWER:
                return 2;
            case ZA_ANSWER:
                return 3;
            case DATE_CREATED_NEW_OLD:
                return 4;
            case DATE_CREATED_OLD_NEW:
                return 5;
            case DATE_MODIFIED_NEW_OLD:
                return 6;
            case DATE_MODIFIED_OLD_NEW:
                return 7;
            case NB_DECK:
                return 8;
        }
        return 0;
    }

    public int getDefaultSort(){
        return Database.mUserDao.fetchUser().getDefaultSortingPosition();
    }

    public void changeStateByPosition(int position) {
        switch (position) {
            // Question: A-Z
            case 0:
                changeSate(SortingStateManager.SortingStates.AZ_QUESTION);
                break;
            // Question: Z-A
            case 1:
                changeSate(SortingStateManager.SortingStates.ZA_QUESTION);
                break;
            // Answer: A-Z
            case 2:
                changeSate(SortingStateManager.SortingStates.AZ_ANSWER);
                break;
            // Answer: Z-A
            case 3:
                changeSate(SortingStateManager.SortingStates.ZA_ANSWER);
                break;
            // Date Created NEW-OLD
            case 4:
                changeSate(SortingStateManager.SortingStates.DATE_CREATED_NEW_OLD);
                break;
            // Date Created OLD-NEW
            case 5:
                changeSate(SortingStateManager.SortingStates.DATE_CREATED_OLD_NEW);
                break;
            // Date Modified NEW-OLD
            case 6:
                changeSate(SortingStateManager.SortingStates.DATE_MODIFIED_NEW_OLD);
                break;
            // Date Modified OLD-NEW
            case 7:
                changeSate(SortingStateManager.SortingStates.DATE_MODIFIED_OLD_NEW);
                break;
            case 8:
                changeSate(SortingStateManager.SortingStates.NB_DECK);
                break;
        }
    }

}
