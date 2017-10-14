package stelztech.youknowehv4.database.deck;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IDeckSchema {

    // COLUMNS NAME
    String TABLE_NAME = "package";
    String COLUMN_NAME_DECK_ID = "deck_id";
    String COLUMN_NAME_DECK_NAME = "deck_name";
    String COLUMN_NAME_DATE_CREATED = "date_created";
    String COLUMN_NAME_DATE_MODIFIED = "date_modified";
    String COLUMN_NAME_PROFILE_ID = "profile_id";
    String COLUMN_NAME_POSITION = "deck_position";

    // ON CREATE
    String SQL_CREATE_TABLE_DECK = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COLUMN_NAME_DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_DECK_NAME + " TEXT NOT NULL,"
            + COLUMN_NAME_DATE_CREATED + " DATE,"
            + COLUMN_NAME_DATE_MODIFIED + " DATE,"
            + COLUMN_NAME_PROFILE_ID + " INTEGER,"
            + COLUMN_NAME_POSITION + " INTEGER,"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.TABLE_NAME + "(" + IProfileSchema.COLUMN_NAME_PROFILE_ID + ")" + " );";
    
    // ON DELETE
    String SQL_DELETE_TABLE_DECK = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // COLUMNS
    String[] DECK_COLUMNS = new String[] { COLUMN_NAME_DECK_ID,
            COLUMN_NAME_DECK_NAME, COLUMN_NAME_DATE_CREATED, COLUMN_NAME_DATE_MODIFIED, COLUMN_NAME_PROFILE_ID,
            COLUMN_NAME_POSITION};
}
