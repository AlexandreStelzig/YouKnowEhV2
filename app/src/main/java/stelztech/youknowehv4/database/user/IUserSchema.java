package stelztech.youknowehv4.database.user;

import stelztech.youknowehv4.database.profile.IProfileSchema;

/**
 * Created by alex on 10/14/2017.
 */

public interface IUserSchema {

    // COLUMNS NAME
    String USER_TABLE = "user_table";
    String COLUMN_USER_ID = "user_id";
    String COLUMN_DATE_CREATED = "date_created";
    String COLUMN_DEFAULT_SORTING = "default_sorting";
    String COLUMN_ACTIVE_PROFILE_ID = "active_profile";
    String COLUMN_ALLOW_PROFILE_DELETION = "allow_profile_deletion";
    String COLUMN_DISPLAY_ALL_CARDS = "display_all_cards";
    String COLUMN_DISPLAY_SPECIFIC_DECK = "display_specific_deck";
    String COLUMN_ALLOW_REVIEW_ALL = "all_review_all";
    String COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED = "allow_search_on_query_change";
    String COLUMN_QUICK_TOGGLE_REVIEW = "quick_toggle_review";

    // ON CREATE
  String SQL_CREATE_TABLE_USER = "CREATE TABLE "
            + USER_TABLE + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DEFAULT_SORTING + " INTEGER NOT NULL,"
            + COLUMN_ALLOW_PROFILE_DELETION + " BOOLEAN,"
            + COLUMN_DISPLAY_ALL_CARDS + " BOOLEAN,"
            + COLUMN_DISPLAY_SPECIFIC_DECK + " BOOLEAN,"
            + COLUMN_QUICK_TOGGLE_REVIEW + " INTEGER,"
            + COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED + " BOOLEAN,"
            + COLUMN_ALLOW_REVIEW_ALL + " BOOLEAN,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + COLUMN_ACTIVE_PROFILE_ID + " INTEGER,"
            + " FOREIGN KEY " + "(" + COLUMN_ACTIVE_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.PROFILE_TABLE + "(" + IProfileSchema.COLUMN_PROFILE_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_USER = "DROP TABLE IF EXISTS " + USER_TABLE;

    // COLUMNS
    String[] USER_COLUMNS = new String[]{COLUMN_USER_ID,
            COLUMN_DEFAULT_SORTING, COLUMN_DATE_CREATED, COLUMN_ALLOW_PROFILE_DELETION, COLUMN_DISPLAY_ALL_CARDS,
            COLUMN_DISPLAY_SPECIFIC_DECK, COLUMN_QUICK_TOGGLE_REVIEW, COLUMN_ALLOW_SEARCH_ON_QUERY_CHANGED, COLUMN_ALLOW_REVIEW_ALL,
            COLUMN_DATE_CREATED, COLUMN_ACTIVE_PROFILE_ID};
}
