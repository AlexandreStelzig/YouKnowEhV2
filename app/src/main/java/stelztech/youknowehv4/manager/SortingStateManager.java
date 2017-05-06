package stelztech.youknowehv4.manager;

import java.text.Collator;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import stelztech.youknowehv4.helper.Helper;
import stelztech.youknowehv4.model.Card;
import stelztech.youknowehv4.model.Deck;

/**
 * Created by alex on 2017-05-06.
 */

public class SortingStateManager {

    public enum SortingStates {
        AZ_QUESTION,
        ZA_QUESTION,
        AZ_ANSWER,
        ZA_ANSWER,
        DATE_CREATED,
        DATE_MODIFIED
    }

    private SortingStates currentState;

    private static SortingStateManager instance;

    private SortingStateManager() {

        currentState = SortingStates.AZ_QUESTION;

    }

    ;

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
            case DATE_CREATED:
                cardListSorted = null;
                break;
            case DATE_MODIFIED:
                cardListSorted = null;
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




    public List<Deck> sortAlphabetically_Deck_Question(List<Deck> list) {

        Collections.sort(list, new Comparator<Deck>() {
            @Override
            public int compare(Deck o1, Deck o2) {

                String lowerCase1 = o1.getDeckName().toLowerCase();
                String lowerCase2 = o2.getDeckName().toLowerCase();

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

    private List<Card> sortByDateCreated(List<Card> list) {
//        Collections.sort(list, new Comparator<Card>() {
//            @Override
//            public int compare(Card o1, Card o2) {
//
//                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
//                Date date1 = null;
//                try {
//                    Date date1 = dateFormat.parse(Helper.getInstance().getDateFormatted(o1.getDateCreated()));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                    return -1;
//                }
//                String o2String = o2.getAnswer().toLowerCase();
//
//                String o1StringPart = o1String.replaceAll("\\d", "");
//                String o2StringPart = o2String.replaceAll("\\d", "");
//
//
//                if (o1StringPart.equalsIgnoreCase(o2StringPart)) {
//                    return extractInt(o1String) - extractInt(o2String);
//                }
//                return o1String.compareTo(o2String);
//            }
//
//            int extractInt(String s) {
//                String num = s.replaceAll("\\D", "");
//                // return 0 if no digits found
//                return num.isEmpty() ? 0 : Integer.parseInt(num);
//            }
//        });
        return list;
    }

    public int getSelectedPosition(){
        switch (currentState) {
            case AZ_QUESTION:
                return 0;
            case ZA_QUESTION:
                return 1;
            case AZ_ANSWER:
                return 2;
            case ZA_ANSWER:
                return 3;
            case DATE_CREATED:
                return 4;
            case DATE_MODIFIED:
                return 5;
        }
        return 0;
    }


}
