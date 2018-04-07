package in.ashnehete.petrolwallet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import in.ashnehete.petrolwallet.adapters.TransactionAdapter;
import in.ashnehete.petrolwallet.dao.TransactionDao;
import in.ashnehete.petrolwallet.entities.Transaction;

public class MainActivity extends AppCompatActivity {

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
        adapter = new TransactionAdapter(new ArrayList<Transaction>());
        recyclerTransactions.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
        // Fetch data from database on another thread
//        new Thread() {
//            @Override
//            public void run() {
//                Log.d(TAG, "getTransactionThread");
//
//                // Notify adapter change on the main thread
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }.start();
        new GetTransactionsTask().execute();
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

    private class GetTransactionsTask extends AsyncTask<Void, Integer, List<Transaction>> {

        @Override
        protected List<Transaction> doInBackground(Void... voids) {
            return transactionDao.getAll();
        }

        @Override
        protected void onPostExecute(List<Transaction> transactions) {
            adapter.updateTransactions(transactions);
        }
    }
}
