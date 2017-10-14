package stelztech.youknowehv4.database.user;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IUserSchema {

    // COLUMNS NAME
    String TABLE_NAME = "user";
    String COLUMN_NAME_USER_ID = "user_id";
    String COLUMN_NAME_DATE_CREATED = "date_created";
    String COLUMN_NAME_DEFAULT_SORTING = "default_sorting";
    String COLUMN_NAME_ACTIVE_PROFILE_ID = "active_profile";
    String COLUMN_NAME_ALLOW_PROFILE_DELETION = "allow_profile_deletion";
    String COLUMN_NAME_DISPLAY_ALL_CARDS = "display_all_cards";
    String COLUMN_NAME_DISPLAY_SPECIFIC_DECK = "display_specific_deck";
    String COLUMN_NAME_ALLOW_PRACTICE_ALL = "all_practice_all";
    String COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED = "allow_search_on_query_change";
    String COLUMN_NAME_QUICK_TOGGLE_REVIEW = "quick_toggle_review";

    // ON CREATE
  String SQL_CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_NAME + " ("
            + COLUMN_NAME_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME_DEFAULT_SORTING + " INTEGER NOT NULL,"
            + COLUMN_NAME_ALLOW_PROFILE_DELETION + " BOOLEAN,"
            + COLUMN_NAME_DISPLAY_ALL_CARDS + " BOOLEAN,"
            + COLUMN_NAME_DISPLAY_SPECIFIC_DECK + " BOOLEAN,"
            + COLUMN_NAME_QUICK_TOGGLE_REVIEW + " INTEGER,"
            + COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED + " BOOLEAN,"
            + COLUMN_NAME_ALLOW_PRACTICE_ALL + " BOOLEAN,"
            + COLUMN_NAME_DATE_CREATED + " DATE,"
            + COLUMN_NAME_ACTIVE_PROFILE_ID + " INTEGER,"
            + " FOREIGN KEY " + "(" + COLUMN_NAME_ACTIVE_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.TABLE_NAME + "(" + IProfileSchema.COLUMN_NAME_PROFILE_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_USER = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // COLUMNS
    String[] USER_COLUMNS = new String[]{COLUMN_NAME_USER_ID,
            COLUMN_NAME_DEFAULT_SORTING, COLUMN_NAME_DATE_CREATED, COLUMN_NAME_ALLOW_PROFILE_DELETION, COLUMN_NAME_DISPLAY_ALL_CARDS,
            COLUMN_NAME_DISPLAY_SPECIFIC_DECK, COLUMN_NAME_QUICK_TOGGLE_REVIEW, COLUMN_NAME_ALLOW_SEARCH_ON_QUERY_CHANGED, COLUMN_NAME_ALLOW_PRACTICE_ALL,
            COLUMN_NAME_DATE_CREATED, COLUMN_NAME_ACTIVE_PROFILE_ID};
}
