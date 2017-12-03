package stelztech.youknowehv4.database.profile;

import stelztech.youknowehv4.database.quiz.IQuizSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IProfileSchema {

    // COLUMNS NAME
    String PROFILE_TABLE = "profile_table";
    String COLUMN_PROFILE_ID = "profile_id";
    String COLUMN_PROFILE_NAME = "profile_name";
    String COLUMN_DATE_CREATED = "date_created";
    String COLUMN_DATE_MODIFIED = "date_modified";
    String COLUMN_QUESTION_LABEL = "question_label";
    String COLUMN_ANSWER_LABEL = "answer_label";
    String COLUMN_ACTIVE_QUIZ_ID = "active_quiz_id";
    String COLUMN_PROFILE_COLOR = "profile_color";
    String COLUMN_PROFILE_LAST_TIME_OPENED = "last_time_opened";
    String COLUMN_PROFILE_IMAGE = "profile_image";

    // ON CREATE
    String SQL_CREATE_TABLE_PROFILE = "CREATE TABLE "
            + PROFILE_TABLE + " ("
            + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PROFILE_NAME + " TEXT NOT NULL,"
            + COLUMN_QUESTION_LABEL + " TEXT NOT NULL,"
            + COLUMN_ANSWER_LABEL + " TEXT NOT NULL,"
            + COLUMN_ACTIVE_QUIZ_ID + " INTEGER,"
            + COLUMN_PROFILE_IMAGE + " INTEGER,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + COLUMN_DATE_MODIFIED + " TEXT,"
            + COLUMN_PROFILE_LAST_TIME_OPENED + " TEXT,"
            + COLUMN_PROFILE_COLOR + " TEXT,"
            + " FOREIGN KEY " + "(" + COLUMN_ACTIVE_QUIZ_ID + ")"
            + " REFERENCES " + IQuizSchema.QUIZ_TABLE + "(" + IQuizSchema.COLUMN_QUIZ_ID + ")" + " );";

    // ON DELETE
    String SQL_DELETE_TABLE_PROFILE = "DROP TABLE IF EXISTS " + PROFILE_TABLE;

}
