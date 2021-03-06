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
    String COLUMN_REVERSE = "quiz_orientation_reverse";
    String COLUMN_REVIEW_ONLY = "review_cards_only";
    String COLUMN_MODE = "quiz_mode";
    String COLUMN_PROFILE_ID = "profile_id";
    String COLUMN_NUM_PASSED = "quiz_num_passed";
    String COLUMN_NUM_FAILED = "quiz_num_failed";
    String COLUMN_NUM_SKIPPED = "quiz_num_skip";
    String COLUMN_STATE = "quiz_state";

    // ON CREATE
    String SQL_CREATE_TABLE_QUIZ = "CREATE TABLE "
            + QUIZ_TABLE + " ("
            + COLUMN_QUIZ_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + COLUMN_DATE_FINISHED + " TEXT,"
            + COLUMN_MODE + " TEXT,"
            + COLUMN_STATE + " TEXT,"
            + COLUMN_PROFILE_ID + " INTEGER,"
            + COLUMN_NUM_PASSED + " INTEGER,"
            + COLUMN_NUM_FAILED + " INTEGER,"
            + COLUMN_NUM_SKIPPED + " INTEGER,"
            + COLUMN_REVIEW_ONLY + " BOOLEAN,"
            + COLUMN_REVERSE + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + COLUMN_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.PROFILE_TABLE + "(" + IProfileSchema.COLUMN_PROFILE_ID + ")" + " );";

    // ON DELETE
    String SQL_DELETE_TABLE_QUIZ = "DROP TABLE IF EXISTS " + QUIZ_TABLE;

}
