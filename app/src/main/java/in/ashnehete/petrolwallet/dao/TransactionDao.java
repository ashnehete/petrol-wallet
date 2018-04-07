package in.ashnehete.petrolwallet.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import in.ashnehete.petrolwallet.entities.Transaction;

/**
 * Created by Aashish Nehete on 01-Apr-18.
 */

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions")
    public List<Transaction> getAll();

    @Insert
    public long insert(Transaction transaction);

    @Update
    public void update(Transaction transaction);

    @Delete
    public void delete(Transaction transaction);
}
