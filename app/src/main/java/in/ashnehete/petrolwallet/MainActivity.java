package in.ashnehete.petrolwallet;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.ashnehete.petrolwallet.adapters.TransactionAdapter;
import in.ashnehete.petrolwallet.dao.TransactionDao;
import in.ashnehete.petrolwallet.entities.Transaction;
import in.ashnehete.petrolwallet.util.RecyclerItemClickListener;

public class MainActivity extends AppCompatActivity implements
        NewTransactionDialogFragment.OnTransactionAddedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.recycler_transactions)
    RecyclerView recyclerTransactions;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    TransactionDao transactionDao;
    TransactionAdapter adapter;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        transactionDao = AppDatabase.getInstance(this).getTransactionDao();
        setupRecyclerTransactions();

        new GetTransactionsTask().execute();
    }

    private void setupRecyclerTransactions() {
        Log.i(TAG, "setupRecyclerTransactions");
        recyclerTransactions.setHasFixedSize(true);

        // Layout Manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerTransactions.setLayoutManager(layoutManager);

        // Decoration
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                recyclerTransactions.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerTransactions.addItemDecoration(itemDecoration);

        // Adapter
        adapter = new TransactionAdapter();
        recyclerTransactions.setAdapter(adapter);

        recyclerTransactions.addOnItemTouchListener(new RecyclerItemClickListener(
                this, recyclerTransactions,
                new RecyclerItemClickListener.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // Nothing
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // Show menu
                        Transaction transaction = adapter.getTransactions(position);
                        Log.d(TAG, "onLongItemClick: " + transaction.toString());
                        showDialog(transaction);
                    }
                }
        ));
    }

    private void showDialog(final Transaction transaction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_alert_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new DeleteTransactionTask().execute(transaction);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    private void testDb() {
        transactionDao.insert(new Transaction(new Date(), 400, 80.00d));
        Log.d(TAG, "insert");

        List<Transaction> transactions = transactionDao.getAll();
        Log.d(TAG, transactions.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    public void addTransaction(View view) {
        Log.i(TAG, "addTransaction");
        FragmentManager fragmentManager = getSupportFragmentManager();
        NewTransactionDialogFragment dialogFragment = new NewTransactionDialogFragment();
        fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                        android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .add(android.R.id.content, dialogFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onTransactionAdded(Date date, int amount, double rate) {
        Transaction transaction = new Transaction(date, amount, rate);
        Log.d(TAG, "Transaction Added: " + transaction.toString());
        new InsertTransactionTask().execute(transaction);
    }

    private class GetTransactionsTask extends AsyncTask<Void, Integer, List<Transaction>> {

        @Override
        protected List<Transaction> doInBackground(Void... voids) {
            return transactionDao.getAll();
        }

        @Override
        protected void onPostExecute(List<Transaction> transactions) {
            adapter.setTransactions(transactions);
        }
    }

    private class InsertTransactionTask extends AsyncTask<Transaction, Integer, Long> {

        @Override
        protected Long doInBackground(Transaction... transactions) {
            return transactionDao.insert(transactions[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            Log.d(TAG, "insert: " + aLong);
            new GetTransactionsTask().execute();
        }
    }

    private class DeleteTransactionTask extends AsyncTask<Transaction, Integer, Void> {

        @Override
        protected Void doInBackground(Transaction... transactions) {
            transactionDao.delete(transactions[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Transaction Deleted", Toast.LENGTH_SHORT)
                    .show();
            new GetTransactionsTask().execute();
        }
    }
}
