package stelztech.youknowehv4.database.quizcard;

import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.quiz.IQuizSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizCardSchema {

    // COLUMNS NAME
    String QUIZ_CARD_TABLE = "quiz_card_table";
    String COLUMN_CARD_ID = "card_id";
    String COLUMN_QUIZ_ID = "quiz_id";
    String COLUMN_CARD_QUESTION = "quiz_card_question";
    String COLUMN_CARD_ANSWER = "quiz_card_answer";
    String COLUMN_NUMBER_FAILED = "quiz_card_num_fail";
    String COLUMN_PASSED = "quiz_card_passed";
    String COLUMN_POSITION = "quiz_card_position";


    // ON CREATE
    String SQL_CREATE_TABLE_QUIZ_CARD = "CREATE TABLE "
            + QUIZ_CARD_TABLE + " ("
            + COLUMN_CARD_ID + " INTEGER NOT NULL,"
            + COLUMN_QUIZ_ID + " INTEGER NOT NULL,"
            + COLUMN_CARD_QUESTION + " TEXT NOT NULL,"
            + COLUMN_CARD_ANSWER + " TEXT NOT NULL,"
            + COLUMN_NUMBER_FAILED + " INTEGER,"
            + COLUMN_POSITION + " INTEGER,"
            + COLUMN_PASSED + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + COLUMN_QUIZ_ID + ")"
            + " REFERENCES " + IQuizSchema.QUIZ_TABLE + "(" + IQuizSchema.COLUMN_QUIZ_ID + ")"
            + " PRIMARY KEY (" + COLUMN_CARD_ID + ", " + COLUMN_QUIZ_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_QUIZ_CARD = "DROP TABLE IF EXISTS " + QUIZ_CARD_TABLE;

}
