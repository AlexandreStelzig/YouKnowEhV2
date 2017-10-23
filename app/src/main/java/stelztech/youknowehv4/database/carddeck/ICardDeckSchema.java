package stelztech.youknowehv4.database.carddeck;

import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.deck.IDeckSchema;
import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface ICardDeckSchema {

    // COLUMNS NAME
    String CARD_DECK_TABLE = "card_deck_table";
    String COLUMN_DECK_ID = "deck_id";
    String COLUMN_CARD_ID = "card_id";
    String COLUMN_DATE_ADDED = "date_added";
    String COLUMN_IS_PRACTICE = "is_practice";
    String COLUMN_DATE_TOGGLE_PRACTICE = "date_toggle_practice";
    
    // ON CREATE
    String SQL_CREATE_TABLE_CARD_DECK = "CREATE TABLE "
            + CARD_DECK_TABLE + " ("
            + COLUMN_CARD_ID + " INTEGER,"
            + COLUMN_DECK_ID + " INTEGER,"
            + COLUMN_IS_PRACTICE + " BOOLEAN,"
            + COLUMN_DATE_TOGGLE_PRACTICE + " DATE,"
            + COLUMN_DATE_ADDED + " DATE,"
            + " FOREIGN KEY " + "(" + COLUMN_CARD_ID + ")"
            + " REFERENCES " + ICardSchema.CARD_TABLE + "(" + ICardSchema.COLUMN_CARD_ID + "),"
            + " FOREIGN KEY " + "(" + COLUMN_DECK_ID + ")"
            + " REFERENCES " + IDeckSchema.DECK_TABLE + "(" + IDeckSchema.COLUMN_DECK_ID + ")"
            + " PRIMARY KEY (" + COLUMN_CARD_ID + ", " + COLUMN_DECK_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_CARD_DECK = "DROP TABLE IF EXISTS " + CARD_DECK_TABLE;

    // COLUMNS
    String[] CARDDECK_COLUMNS = new String[] { COLUMN_DECK_ID,
            COLUMN_CARD_ID, COLUMN_DATE_ADDED, COLUMN_IS_PRACTICE, COLUMN_DATE_TOGGLE_PRACTICE };
}
