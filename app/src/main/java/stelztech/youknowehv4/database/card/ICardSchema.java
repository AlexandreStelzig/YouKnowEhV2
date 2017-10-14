package stelztech.youknowehv4.database.card;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface  ICardSchema {

    // COLUMNS NAME
    String TABLE_NAME = "card";
    String COLUMN_NAME_CARD_ID = "card_id";
    String COLUMN_NAME_QUESTION = "question";
    String COLUMN_NAME_ANSWER = "answer";
    String COLUMN_NAME_MORE_INFORMATION = "more_info";
    String COLUMN_NAME_DATE_CREATED = "date_created";
    String COLUMN_NAME_DATE_MODIFIED = "date_modified";
    String COLUMN_NAME_PROFILE_ID = "profile_id";
    String COLUMN_NAME_ARCHIVED = "archived";

    // ON CREATE
    String SQL_CREATE_TABLE_CARD = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COLUMN_NAME_CARD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_QUESTION + " TEXT NOT NULL,"
            + COLUMN_NAME_ANSWER + " TEXT NOT NULL,"
            + COLUMN_NAME_MORE_INFORMATION + " TEXT,"
            + COLUMN_NAME_DATE_CREATED + " DATE,"
            + COLUMN_NAME_DATE_MODIFIED + " DATE,"
            + COLUMN_NAME_ARCHIVED + " BOOLEAN,"
            + COLUMN_NAME_PROFILE_ID + " INTEGER,"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.TABLE_NAME + "(" + IProfileSchema.COLUMN_NAME_PROFILE_ID + ")" + " );";

    // ON DELETE
    String SQL_DELETE_TABLE_CARD = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // COLUMNS
    String[] CARD_COLUMNS = new String[] { COLUMN_NAME_CARD_ID,
            COLUMN_NAME_QUESTION, COLUMN_NAME_ANSWER, COLUMN_NAME_MORE_INFORMATION, COLUMN_NAME_DATE_CREATED,
            COLUMN_NAME_DATE_MODIFIED, COLUMN_NAME_PROFILE_ID, COLUMN_NAME_ARCHIVED };
}
