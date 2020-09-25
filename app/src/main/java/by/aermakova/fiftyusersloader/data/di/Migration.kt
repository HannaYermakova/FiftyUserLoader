package by.aermakova.fiftyusersloader.data.di

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE user_table ADD COLUMN version INTEGER DEFAULT 2 not null")
    }
}
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE user_table_new (id INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0 not null, gender TEXT NOT NULL, name TEXT NOT NULL, email TEXT NOT NULL, login TEXT NOT NULL, phone TEXT NOT NULL, picture TEXT NOT NULL, imageExtension TEXT NOT NULL)")
        database.execSQL("INSERT INTO user_table_new (id, gender, name, email, login, phone, picture, imageExtension) SELECT id, gender, name, email, login, phone, picture, imageExtension FROM user_table")
        database.execSQL("DROP TABLE user_table")
        database.execSQL("ALTER TABLE user_table_new RENAME to user_table")
    }
}