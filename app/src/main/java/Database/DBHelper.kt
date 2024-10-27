package Database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.util.Log
import models.PopularEventModel

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val CREATE_TABLE_USERDATA = """
        CREATE TABLE Userdata (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT UNIQUE NOT NULL,
            firstname TEXT NOT NULL,
            lastname TEXT NOT NULL,
            password TEXT NOT NULL
        )
    """

    companion object {
        private const val DATABASE_VERSION = 5 // Ensure this version is consistent to apply changes
        private const val DATABASE_NAME = "Userdata.db"
        private const val TABLE_EVENTS = "Events"

        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_EVENT_NAME = "event_name"
        private const val COLUMN_EVENT_TYPE = "event_type"
        private const val COLUMN_EVENT_DATE = "event_date"
        private const val COLUMN_EVENT_START = "event_start"
        private const val COLUMN_EVENT_FINISH = "event_finish"
        private const val COLUMN_EVENT_INFO = "event_info"
        private const val COLUMN_IMAGE_URI = "image_uri"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableEvents = """
            CREATE TABLE $TABLE_EVENTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER,
                $COLUMN_EVENT_NAME TEXT NOT NULL,
                $COLUMN_EVENT_TYPE TEXT NOT NULL,
                $COLUMN_EVENT_DATE TEXT NOT NULL,
                $COLUMN_EVENT_START TEXT NOT NULL,
                $COLUMN_EVENT_FINISH TEXT NOT NULL,
                $COLUMN_EVENT_INFO TEXT NOT NULL,
                $COLUMN_IMAGE_URI TEXT
            )
        """
        db?.execSQL(CREATE_TABLE_USERDATA) // Create Userdata table
        db?.execSQL(createTableEvents) // Create Events table
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("ALTER TABLE $TABLE_EVENTS ADD COLUMN $COLUMN_USER_ID INTEGER")
        }
    }

    // Method to insert user data
    fun insertData(
        username: String,
        firstname: String,
        lastname: String,
        password: String
    ): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put("username", username)
            put("firstname", firstname)
            put("lastname", lastname)
            put("password", password)
        }
        val result = db.insert("Userdata", null, cv)
        db.close()
        return result != -1L // Return true if insert was successful
    }

    // Validate user credentials
    fun validateUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM Userdata WHERE username = ? AND password = ?",
            arrayOf(username, password)
        )

        val isValid = cursor.count > 0
        cursor.close() // Close cursor to prevent memory leaks
        return isValid
    }

    // Method to insert an event
    fun insertEvent(
        eventName: String,
        eventType: String,
        eventDate: String,
        eventStart: String,
        eventFinish: String,
        eventInfo: String,
        imageUri: String?, // Use String type for image URI
        userId: Int // Use Int type for userId
    ): Boolean {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(COLUMN_EVENT_NAME, eventName)
            put(COLUMN_EVENT_TYPE, eventType)
            put(COLUMN_EVENT_DATE, eventDate)
            put(COLUMN_EVENT_START, eventStart)
            put(COLUMN_EVENT_FINISH, eventFinish)
            put(COLUMN_EVENT_INFO, eventInfo)
            put(COLUMN_IMAGE_URI, imageUri) // Directly store String for URI
            put(COLUMN_USER_ID, userId) // Include userId in the ContentValues
        }
        val result = db.insert(TABLE_EVENTS, null, cv)
        db.close()
        return result != -1L
    }


    // Method to read all events
    fun readAllEvents(): List<PopularEventModel> {
        val eventsList = mutableListOf<PopularEventModel>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EVENTS", null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val eventIdIndex = cursor.getColumnIndex(COLUMN_ID)
                    val eventNameIndex = cursor.getColumnIndex(COLUMN_EVENT_NAME)
                    val eventTypeIndex = cursor.getColumnIndex(COLUMN_EVENT_TYPE)

                    if (eventIdIndex != -1 && eventNameIndex != -1 && eventTypeIndex != -1) {
                        val event = PopularEventModel().apply {
                            setEventId(cursor.getInt(eventIdIndex))
                            setEventName(cursor.getString(eventNameIndex))
                            setEventType(cursor.getString(eventTypeIndex))
                        }
                        eventsList.add(event)
                    } else {
                        Log.e("DBHelper", "Column not found")
                    }
                } while (cursor.moveToNext())
            } else {
                Log.e("DBHelper", "No events found in the database.")
            }
        } finally {
            cursor.close() // Always close the cursor
        }

        return eventsList
    }
}
