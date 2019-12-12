package ovh.geoffrey_druelle.nantestoilettes.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ovh.geoffrey_druelle.nantestoilettes.data.local.converter.BooleanConverter
import ovh.geoffrey_druelle.nantestoilettes.data.local.dao.ToiletDao
import ovh.geoffrey_druelle.nantestoilettes.data.local.database.AppDatabase.Companion.databaseVersion
import ovh.geoffrey_druelle.nantestoilettes.data.local.model.Toilet

@Database(
    entities = [Toilet::class],
    version = databaseVersion,
    exportSchema = false
)
@TypeConverters(BooleanConverter::class)
abstract class AppDatabase: RoomDatabase(){
    abstract fun toiletDao(): ToiletDao

    companion object{
        @Volatile
        private var instance: AppDatabase? = null

        private const val DB_NAME = "Nantes_Toilettes_Database"

        const val databaseVersion = 1

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this){
                instance ?: build(context).also { instance = it }
            }

        private fun build(context: Context) =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DB_NAME)
                .addMigrations(nToNPlusOneMigration)
                .build()

        private val nToNPlusOneMigration = object : Migration(databaseVersion, databaseVersion+1){
            override fun migrate(database: SupportSQLiteDatabase) {}
        }
    }
}
