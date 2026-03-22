package com.arijeet.quickfixapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings

@Database(entities = [Category::class, Issue::class, HistoryEntry::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class QuickFixDatabase : RoomDatabase() {
    abstract fun dao(): QuickFixDao

    companion object {
        @Volatile
        private var INSTANCE: QuickFixDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): QuickFixDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QuickFixDatabase::class.java,
                    "quickfix_database"
                )
                .addCallback(QuickFixDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class QuickFixDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.dao())
                }
            }
        }

        suspend fun populateDatabase(dao: QuickFixDao) {
            val categories = listOf(
                Category("battery", "Battery", android.R.drawable.ic_lock_idle_low_battery),
                Category("storage", "Storage", android.R.drawable.ic_menu_save),
                Category("performance", "Performance", android.R.drawable.ic_menu_manage),
                Category("apps", "Apps", android.R.drawable.ic_menu_info_details),
                Category("internet", "Internet", android.R.drawable.ic_menu_share)
            )
            dao.insertCategories(categories)

            val issues = listOf(
                // Battery
                Issue(
                    "bat_drain", "battery", "Battery draining fast",
                    "Apps running in background or high screen brightness can drain battery quickly.",
                    listOf("Check battery usage in settings", "Reduce screen brightness", "Close unused background apps", "Enable Adaptive Battery"),
                    Settings.ACTION_BATTERY_SAVER_SETTINGS
                ),
                Issue(
                    "bat_heat", "battery", "Phone heating",
                    "Heavy gaming, multi-tasking, or direct sunlight can cause the phone to heat up.",
                    listOf("Stop using the phone for a while", "Remove the phone case", "Disable GPS and Bluetooth if not needed", "Check for software updates"),
                    Settings.ACTION_DISPLAY_SETTINGS
                ),
                Issue(
                    "bat_slow", "battery", "Charging slow",
                    "A faulty cable, dirty charging port, or weak power source can slow down charging.",
                    listOf("Clean the charging port gently", "Use the original charger and cable", "Avoid using the phone while charging"),
                    null
                ),
                // Storage
                Issue(
                    "store_full", "storage", "Storage full",
                    "Large files, system updates, and cached data occupy significant space.",
                    listOf("Clear app cache", "Delete old downloads", "Move photos to cloud storage", "Uninstall apps you don't use"),
                    Settings.ACTION_INTERNAL_STORAGE_SETTINGS
                ),
                Issue(
                    "store_media", "storage", "Too many media files",
                    "Photos and videos can quickly fill up your internal storage.",
                    listOf("Review and delete duplicate photos", "Back up media to Google Photos", "Delete large video files"),
                    null
                ),
                Issue(
                    "store_cache", "storage", "Cache taking space",
                    "Apps store temporary files (cache) to load faster, but it grows over time.",
                    listOf("Go to App Info in settings", "Select the app with high usage", "Tap 'Clear Cache'"),
                    Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
                ),
                // Performance
                Issue(
                    "perf_slow", "performance", "Phone slow",
                    "Low RAM or too many background processes can slow down your device.",
                    listOf("Restart your device", "Uninstall unused apps", "Update system software", "Reduce system animations"),
                    Settings.ACTION_DEVICE_INFO_SETTINGS
                ),
                Issue(
                    "perf_hang", "performance", "Phone hanging",
                    "Software bugs or heavy system load can cause the device to become unresponsive.",
                    listOf("Force restart your device", "Check for problematic apps", "Ensure enough free storage space"),
                    null
                ),
                // Apps
                Issue(
                    "app_crash", "apps", "App crashing",
                    "Outdated apps or corrupted cache can lead to crashes.",
                    listOf("Update the app from Play Store", "Clear app data and cache", "Reinstall the app", "Check for Android System WebView updates"),
                    Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
                ),
                Issue(
                    "app_not_open", "apps", "App not opening",
                    "Incompatibility or system restrictions might prevent an app from launching.",
                    listOf("Check for app updates", "Verify app permissions", "Clear app data"),
                    Settings.ACTION_APPLICATION_SETTINGS
                ),
                // Internet
                Issue(
                    "net_wifi", "internet", "Wi-Fi not connecting",
                    "Incorrect password, router issues, or saved network errors.",
                    listOf("Toggle Wi-Fi off and on", "Forget and reconnect to the network", "Restart your router", "Reset Network Settings"),
                    Settings.ACTION_WIFI_SETTINGS
                ),
                Issue(
                    "net_data", "internet", "Mobile data not working",
                    "Network coverage, data limits, or APN settings might be the cause.",
                    listOf("Toggle Airplane Mode", "Check your data plan", "Verify mobile data is enabled", "Check APN settings"),
                    Settings.ACTION_DATA_ROAMING_SETTINGS
                )
            )
            dao.insertIssues(issues)
        }
    }
}
