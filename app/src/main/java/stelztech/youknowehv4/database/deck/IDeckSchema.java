package stelztech.youknowehv4.database.deck;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IDeckSchema {

    // COLUMNS NAME
    String DECK_TABLE = "deck_table";
    String COLUMN_DECK_ID = "deck_id";
    String COLUMN_DECK_NAME = "deck_name";
    String COLUMN_DATE_CREATED = "date_created";
    String COLUMN_DATE_MODIFIED = "date_modified";
    String COLUMN_PROFILE_ID = "profile_id";
    String COLUMN_POSITION = "deck_position";

    // ON CREATE
    String SQL_CREATE_TABLE_DECK = "CREATE TABLE "
            + DECK_TABLE + " ("
            + COLUMN_DECK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DECK_NAME + " TEXT NOT NULL,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + COLUMN_DATE_MODIFIED + " TEXT,"
            + COLUMN_PROFILE_ID + " INTEGER,"
            + COLUMN_POSITION + " INTEGER,"
            + " FOREIGN KEY " + "(" + COLUMN_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.PROFILE_TABLE + "(" + IProfileSchema.COLUMN_PROFILE_ID + ")" + " );";
    
    // ON DELETE
    String SQL_DELETE_TABLE_DECK = "DROP TABLE IF EXISTS " + DECK_TABLE;

}
