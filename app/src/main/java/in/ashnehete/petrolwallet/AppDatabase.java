package in.ashnehete.petrolwallet;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import in.ashnehete.petrolwallet.dao.TransactionDao;
import in.ashnehete.petrolwallet.entities.Transaction;

/**
 * Created by Aashish Nehete on 01-Apr-18.
 */

@Database(entities = {Transaction.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "petrol-wallet";
    private static volatile AppDatabase instance;

    static synchronized AppDatabase getInstance(Context context) {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static AppDatabase create(Context context) {
        return Room.databaseBuilder(
                context,
                AppDatabase.class,
                DB_NAME).build();
    }

    public abstract TransactionDao getTransactionDao();
}
