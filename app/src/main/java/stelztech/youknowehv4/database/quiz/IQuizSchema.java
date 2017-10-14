package stelztech.youknowehv4.database.quiz;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizSchema {

    // COLUMNS NAME
    String TABLE_NAME = "quiz";
    String COLUMN_NAME_QUIZ_ID = "quiz_id";
    String COLUMN_NAME_DATE_CREATED = "date_created";
    String COLUMN_NAME_DATE_FINISHED = "date_finished";
    String COLUMN_NAME_REPEAT = "quiz_failed_repeat";
    String COLUMN_NAME_REVERSE = "quiz_orientation_reverse";
    String COLUMN_NAME_MODE = "quiz_mode";
    String COLUMN_NAME_PROFILE_ID = "profile_id";
    String COLUMN_NAME_REVIEW_ONLY = "review_cards_only";

    // ON CREATE
    String SQL_CREATE_TABLE_QUIZ = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COLUMN_NAME_QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_DATE_CREATED + " DATE,"
            + COLUMN_NAME_DATE_FINISHED + " DATE,"
            + COLUMN_NAME_MODE + " TEXT,"
            + COLUMN_NAME_REPEAT + " BOOLEAN,"
            + COLUMN_NAME_PROFILE_ID + " INTEGER,"
            + COLUMN_NAME_REVIEW_ONLY + " BOOLEAN,"
            + COLUMN_NAME_REVERSE + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.TABLE_NAME + "(" + IProfileSchema.COLUMN_NAME_PROFILE_ID + ")" + " );";

    // ON DELETE
    String SQL_DELETE_TABLE_QUIZ = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // COLUMNS
    String[] QUIZ_COLUMNS = new String[]{COLUMN_NAME_QUIZ_ID,
            COLUMN_NAME_DATE_CREATED, COLUMN_NAME_DATE_FINISHED, COLUMN_NAME_REPEAT, COLUMN_NAME_REVERSE,
            COLUMN_NAME_MODE, COLUMN_NAME_PROFILE_ID, COLUMN_NAME_REVIEW_ONLY};
}
