package stelztech.youknowehv4.database.profile;

import stelztech.youknowehv4.database.quiz.IQuizSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IProfileSchema {

   // COLUMNS NAME
   String TABLE_NAME = "profile";
   String COLUMN_NAME_PROFILE_ID = "profile_id";
   String COLUMN_NAME_PROFILE_NAME = "profile_name";
   String COLUMN_NAME_DATE_CREATED = "date_created";
   String COLUMN_NAME_DATE_MODIFIED = "date_modified";
   String COLUMN_NAME_QUESTION_LABEL = "question_label";
   String COLUMN_NAME_ANSWER_LABEL = "answer_label";
   String COLUMN_NAME_ACTIVE_QUIZ_ID = "active_quiz_id";

   // ON CREATE
   String SQL_CREATE_TABLE_PROFILE = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COLUMN_NAME_PROFILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_PROFILE_NAME + " TEXT NOT NULL,"
            + COLUMN_NAME_QUESTION_LABEL + " TEXT NOT NULL,"
            + COLUMN_NAME_ANSWER_LABEL + " TEXT NOT NULL,"
            + COLUMN_NAME_ACTIVE_QUIZ_ID + " INTEGER,"
            + COLUMN_NAME_DATE_CREATED + " DATE,"
            + COLUMN_NAME_DATE_MODIFIED + " DATE,"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_ACTIVE_QUIZ_ID + ")"
            + " REFERENCES " + IQuizSchema.TABLE_NAME + "(" + IQuizSchema.COLUMN_NAME_QUIZ_ID + ")" + " );";

   // ON DELETE
   String SQL_DELETE_TABLE_PROFILE = "DROP TABLE IF EXISTS " + TABLE_NAME;

   // COLUMNS
    String[] PROFILE_COLUMNS = new String[] { COLUMN_NAME_PROFILE_ID,
            COLUMN_NAME_PROFILE_NAME, COLUMN_NAME_DATE_CREATED, COLUMN_NAME_DATE_MODIFIED, COLUMN_NAME_QUESTION_LABEL,
            COLUMN_NAME_ANSWER_LABEL, COLUMN_NAME_ACTIVE_QUIZ_ID };
}
