package stelztech.youknowehv4.database.quizcard;

import stelztech.youknowehv4.database.card.ICardSchema;
import stelztech.youknowehv4.database.quiz.IQuizSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IQuizCardSchema {

    // COLUMNS NAME
    String QUIZ_CARD_TABLE = "quiz_card_table";
    String COLUMN_QUIZ_ID = "quizid";
    String COLUMN_CARD_ID = "cardid";
    String COLUMN_NUM_FAILED = "numfail";
    String COLUMN_PASSED = "passed";
    String COLUMN_POSITION = "position";


    // ON CREATE
    String SQL_CREATE_TABLE_QUIZ_CARD = "CREATE TABLE "
            + QUIZ_CARD_TABLE + " ("
            + COLUMN_CARD_ID + " INTEGER,"
            + COLUMN_QUIZ_ID + " INTEGER,"
            + COLUMN_NUM_FAILED + " INTEGER,"
            + COLUMN_POSITION + " INTEGER,"
            + COLUMN_PASSED + " BOOLEAN,"
            + " FOREIGN KEY " + "(" + COLUMN_CARD_ID + ")"
            + " REFERENCES " + ICardSchema.CARD_TABLE + "(" + ICardSchema.COLUMN_CARD_ID + "),"
            + " FOREIGN KEY " + "(" + COLUMN_QUIZ_ID + ")"
            + " REFERENCES " + IQuizSchema.QUIZ_TABLE + "(" + IQuizSchema.COLUMN_QUIZ_ID + ")"
            + " PRIMARY KEY (" + COLUMN_CARD_ID + ", " + COLUMN_QUIZ_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_QUIZ_CARD = "DROP TABLE IF EXISTS " + QUIZ_CARD_TABLE;

    // COLUMNS
    String[] QUIZCARD_COLUMNS = new String[]{COLUMN_QUIZ_ID,
            COLUMN_CARD_ID, COLUMN_NUM_FAILED, COLUMN_PASSED, COLUMN_POSITION};
}
