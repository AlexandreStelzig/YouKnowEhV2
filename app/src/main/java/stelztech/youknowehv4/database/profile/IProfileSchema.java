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

   // ON CREATE
   String SQL_CREATE_TABLE_PROFILE = "CREATE TABLE "
            + PROFILE_TABLE + " ("
            + COLUMN_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PROFILE_NAME + " TEXT NOT NULL,"
            + COLUMN_QUESTION_LABEL + " TEXT NOT NULL,"
            + COLUMN_ANSWER_LABEL + " TEXT NOT NULL,"
            + COLUMN_ACTIVE_QUIZ_ID + " INTEGER,"
            + COLUMN_DATE_CREATED + " DATE,"
            + COLUMN_DATE_MODIFIED + " DATE,"
            + " FOREIGN KEY " + "(" + COLUMN_ACTIVE_QUIZ_ID + ")"
            + " REFERENCES " + IQuizSchema.QUIZ_TABLE + "(" + IQuizSchema.COLUMN_QUIZ_ID + ")" + " );";

   // ON DELETE
   String SQL_DELETE_TABLE_PROFILE = "DROP TABLE IF EXISTS " + PROFILE_TABLE;

   // COLUMNS
    String[] PROFILE_COLUMNS = new String[] { COLUMN_PROFILE_ID,
            COLUMN_PROFILE_NAME, COLUMN_DATE_CREATED, COLUMN_DATE_MODIFIED, COLUMN_QUESTION_LABEL,
            COLUMN_ANSWER_LABEL, COLUMN_ACTIVE_QUIZ_ID };
}