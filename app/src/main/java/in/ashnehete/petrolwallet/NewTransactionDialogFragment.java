package in.ashnehete.petrolwallet;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.github.badoualy.datepicker.DatePickerTimeline;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewTransactionDialogFragment extends DialogFragment {

    OnTransactionAddedListener callback;

    @BindView(R.id.timeline_date)
    DatePickerTimeline timelineDate;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.et_rate)
    EditText etRate;

    Date selectedDate = new Date();

    public NewTransactionDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_transaction_dialog, container, false);
        ButterKnife.bind(this, rootView);

        timelineDate.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                selectedDate.setYear(year);
                selectedDate.setMonth(month);
                selectedDate.setDate(day);
            }
        });

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @OnClick(R.id.btn_add)
    public void onClickBtnAdd(View view) {
        if (etAmount.getText().toString().equals("")
                || etRate.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter all the fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        int amount = Integer.parseInt(etAmount.getText().toString());
        double rate = Double.parseDouble(etRate.getText().toString());
        callback.onTransactionAdded(selectedDate, amount, rate);

        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            InputMethodManager im =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }

        getActivity().onBackPressed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (OnTransactionAddedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public interface OnTransactionAddedListener {
        public void onTransactionAdded(Date date, int amount, double rate);
    }
}
