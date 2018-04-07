package in.ashnehete.petrolwallet.entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Aashish Nehete on 01-Apr-18.
 */

@Entity(tableName = "transactions")
public class Transaction {
    @PrimaryKey(autoGenerate = true)
    public int id;
    private Date date;
    private int amount;
    private double rate;

    public Transaction(Date date, int amount, double rate) {
        this.date = date;
        this.amount = amount;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getDateString() {
        return new SimpleDateFormat("dd/MM/yy", Locale.US).format(date);
    }

    @Override
    public String toString() {
        return "Date: " + date.toString() + "\tAmount: " + amount + "\tRate: " + rate;
    }
}
