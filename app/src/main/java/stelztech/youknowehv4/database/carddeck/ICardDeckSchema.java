package stelztech.youknowehv4.database.carddeck;

import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.deck.IDeckSchema;
import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface ICardDeckSchema {

    // COLUMNS NAME
    String TABLE_NAME = "carddeck";
    String COLUMN_NAME_DECK_ID = "deck_id";
    String COLUMN_NAME_CARD_ID = "card_id";
    String COLUMN_NAME_DATE_ADDED = "date_added";
    String COLUMN_NAME_IS_PRACTICE = "is_practice";
    String COLUMN_NAME_DATE_TOGGLE_PRACTICE = "date_toggle_practice";
    
    // ON CREATE
    String SQL_CREATE_TABLE_CARD_DECK = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COLUMN_NAME_CARD_ID + " INTEGER,"
            + COLUMN_NAME_DECK_ID + " INTEGER,"
            + COLUMN_NAME_IS_PRACTICE + " BOOLEAN,"
            + COLUMN_NAME_DATE_TOGGLE_PRACTICE + " DATE,"
            + COLUMN_NAME_DATE_ADDED + " DATE,"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_CARD_ID + ")"
            + " REFERENCES " + ICardSchema.TABLE_NAME + "(" + ICardSchema.COLUMN_NAME_CARD_ID + "),"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_DECK_ID + ")"
            + " REFERENCES " + IDeckSchema.TABLE_NAME + "(" + IDeckSchema.COLUMN_NAME_DECK_ID + ")"
            + " PRIMARY KEY (" + COLUMN_NAME_CARD_ID + ", " + COLUMN_NAME_DECK_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_CARD_DECK = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // COLUMNS
    String[] CARDDECK_COLUMNS = new String[] { COLUMN_NAME_DECK_ID,
            COLUMN_NAME_CARD_ID, COLUMN_NAME_DATE_ADDED, COLUMN_NAME_IS_PRACTICE, COLUMN_NAME_DATE_TOGGLE_PRACTICE };
}
