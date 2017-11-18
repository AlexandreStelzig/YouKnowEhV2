package stelztech.youknowehv4.database.carddeck;

import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.deck.IDeckSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface ICardDeckSchema {

    // COLUMNS NAME
    String CARD_DECK_TABLE = "card_deck_table";
    String COLUMN_DECK_ID = "deck_id";
    String COLUMN_CARD_ID = "card_id";
    String COLUMN_DATE_CREATED = "date_created";
    String COLUMN_IS_REVIEW = "is_review";
    String COLUMN_DATE_TOGGLE_REVIEW = "date_toggle_review";
    
    // ON CREATE
    String SQL_CREATE_TABLE_CARD_DECK = "CREATE TABLE "
            + CARD_DECK_TABLE + " ("
            + COLUMN_CARD_ID + " INTEGER NOT NULL,"
            + COLUMN_DECK_ID + " INTEGER NOT NULL,"
            + COLUMN_IS_REVIEW + " BOOLEAN,"
            + COLUMN_DATE_TOGGLE_REVIEW + " TEXT,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + " FOREIGN KEY " + "(" + COLUMN_CARD_ID + ")"
            + " REFERENCES " + ICardSchema.CARD_TABLE + "(" + ICardSchema.COLUMN_CARD_ID + "),"
            + " FOREIGN KEY " + "(" + COLUMN_DECK_ID + ")"
            + " REFERENCES " + IDeckSchema.DECK_TABLE + "(" + IDeckSchema.COLUMN_DECK_ID + ")"
            + " PRIMARY KEY (" + COLUMN_CARD_ID + ", " + COLUMN_DECK_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_CARD_DECK = "DROP TABLE IF EXISTS " + CARD_DECK_TABLE;

    // COLUMNS
    String[] CARD_DECK_COLUMNS = new String[] { COLUMN_DECK_ID,
            COLUMN_CARD_ID, COLUMN_DATE_CREATED, COLUMN_IS_REVIEW, COLUMN_DATE_TOGGLE_REVIEW};
}
