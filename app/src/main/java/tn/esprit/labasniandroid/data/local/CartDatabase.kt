package tn.esprit.labasniandroid.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tn.esprit.labasniandroid.data.local.dao.CartDao
import tn.esprit.labasniandroid.data.local.entities.CartItem

/**
 * Database Room pour le panier (équivalent PersistenceController iOS)
 */
@Database(
    entities = [CartItem::class],
    version = 1,
    exportSchema = false
)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: CartDatabase? = null

        fun getDatabase(context: Context): CartDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartDatabase::class.java,
                    "cart_database"
                )
                    .fallbackToDestructiveMigration() // Pour les migrations simples
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

