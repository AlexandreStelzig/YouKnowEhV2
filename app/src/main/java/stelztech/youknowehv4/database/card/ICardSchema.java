package stelztech.youknowehv4.database.card;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface  ICardSchema {

    // COLUMNS NAME
    String CARD_TABLE = "card_table";
    String COLUMN_CARD_ID = "card_id";
    String COLUMN_QUESTION = "question";
    String COLUMN_ANSWER = "answer";
    String COLUMN_MORE_INFORMATION = "more_info";
    String COLUMN_DATE_CREATED = "date_created";
    String COLUMN_DATE_MODIFIED = "date_modified";
    String COLUMN_PROFILE_ID = "profile_id";
    String COLUMN_ARCHIVED = "archived";

    // ON CREATE
    String SQL_CREATE_TABLE_CARD = "CREATE TABLE "
            + CARD_TABLE + " ("
            + COLUMN_CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_QUESTION + " TEXT NOT NULL,"
            + COLUMN_ANSWER + " TEXT NOT NULL,"
            + COLUMN_MORE_INFORMATION + " TEXT,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + COLUMN_DATE_MODIFIED + " TEXT,"
            + COLUMN_ARCHIVED + " BOOLEAN,"
            + COLUMN_PROFILE_ID + " INTEGER,"
            + " FOREIGN KEY " + "(" + COLUMN_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.PROFILE_TABLE + "(" + IProfileSchema.COLUMN_PROFILE_ID + ")" + " );";

    // ON DELETE
    String SQL_DELETE_TABLE_CARD = "DROP TABLE IF EXISTS " + CARD_TABLE;

}
