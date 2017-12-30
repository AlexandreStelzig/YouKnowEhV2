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
    String COLUMN_ACTIVE_PROFILE_ID = "active_profile";

    // ON CREATE
  String SQL_CREATE_TABLE_USER = "CREATE TABLE "
            + USER_TABLE + " ("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DATE_CREATED + " TEXT,"
            + COLUMN_ACTIVE_PROFILE_ID + " INTEGER,"
            + " FOREIGN KEY " + "(" + COLUMN_ACTIVE_PROFILE_ID + ")"
            + " REFERENCES " + IProfileSchema.PROFILE_TABLE + "(" + IProfileSchema.COLUMN_PROFILE_ID + ") " + ");";

    // ON DELETE
    String SQL_DELETE_TABLE_USER = "DROP TABLE IF EXISTS " + USER_TABLE;

}
