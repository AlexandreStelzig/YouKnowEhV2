package stelztech.youknowehv4.database.quiz;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizSchema {

    // COLUMNS NAME
    String QUIZ_TABLE = "quiz_table";
    String COLUMN_QUIZ_ID = "quiz_id";
    String COLUMN_DATE_CREATED = "date_created";
    String COLUMN_DATE_FINISHED = "date_finished";
    String COLUMN_REPEAT = "quiz_failed_repeat";
    String COLUMN_REVERSE = "quiz_orientation_reverse";
    String COLUMN_MODE = "quiz_mode";
    String COLUMN_PROFILE_ID = "profile_id";
    String COLUMN_REVIEW_ONLY = "review_cards_only";

    // ON CREATE
    String SQL_CREATE_TABLE_QUIZ = "CREATE TABLE "
            + QUIZ_TABLE + " ("
            + COLUMN_QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + COLUMN_DATE_FINISHED + " TEXT,"
            + COLUMN_MODE + " TEXT,"
            + COLUMN_REPEAT + " BOOLEAN,"
            + COLUMN_PROFILE_ID + " INTEGER,"
            + COLUMN_REVIEW_ONLY + " BOOLEAN,"
            + COLUMN_REVERSE + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + COLUMN_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.PROFILE_TABLE + "(" + IProfileSchema.COLUMN_PROFILE_ID + ")" + " );";

    // ON DELETE
    String SQL_DELETE_TABLE_QUIZ = "DROP TABLE IF EXISTS " + QUIZ_TABLE;

}
