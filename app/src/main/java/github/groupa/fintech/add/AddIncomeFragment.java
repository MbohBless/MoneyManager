package github.groupa.fintech.add;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import github.groupa.fintech.database.DatabaseClass;
import github.groupa.fintech.drive.DriveRepo;
import github.groupa.fintech.drive.DriveServiceHelper;
import github.groupa.fintech.statistics.StatisticsModelClass;
import github.groups.fintech.R;

public class AddIncomeFragment extends Fragment {
    public AddIncomeFragment() {
    }

    private Button addIncome;
    private DatePickerDialog datePicker;
    private EditText incomeDate;

    private TimePickerDialog timePicker;
    private EditText incomeTime, amount, comment;
    private String[] selection;
    private DatabaseClass databaseClass;
    DriveServiceHelper dsh;
    boolean isAvailable;
    String derivedName;
    private static Map<String, String> monthsMap = new HashMap<String, String>() {
        {
            put("01", "Jan");
            put("02", "Feb");
            put("03", "Mar");
            put("04", "Apr");
            put("05", "May");
            put("06", "Jun");
            put("07", "Jul");
            put("08", "Aug");
            put("09", "Sep");
            put("10", "Oct");
            put("11", "Nov");
            put("12", "Dec");
        }
    };
    public static String defFilename = "financeReport";

    @SuppressLint("DefaultLocale")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_income, container, false);
        databaseClass = new DatabaseClass(getContext());
        Account account = AccountManager.get(getActivity()).getAccountsByType("com.google")[0];
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(getActivity(), Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(account);
        Drive googleDriveService = new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).setApplicationName("money_manager").build();
        dsh = new DriveServiceHelper(googleDriveService);
        ArrayList<String> titles = databaseClass.getDistinctIncome();
        AutoCompleteTextView incomeSource = root.findViewById(R.id.income_source);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, titles);
        selection = new String[1];
        incomeSource.setAdapter(arrayAdapter);
        incomeSource.setCursorVisible(true);
        incomeSource.setOnItemClickListener((parent, view, position, id) -> {
            incomeSource.showDropDown();
            selection[0] = (String) parent.getItemAtPosition(position);
        });
        incomeSource.setOnClickListener(v -> incomeSource.showDropDown());
        //////////////////////////////////PICK TIME FROM CLOCK
        incomeTime = root.findViewById(R.id.add_income_time);
        incomeTime.setInputType(InputType.TYPE_NULL);
        incomeTime.setOnClickListener(v -> {
            Calendar time = Calendar.getInstance();
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int minute = time.get(Calendar.MINUTE);

            timePicker = new TimePickerDialog(root.getContext(),
                    (view, hourOfDay, minuteOfHour) ->
                            incomeTime.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour)), hour, minute, true);
            timePicker.show();
        });

        //////////////////////////////////PICK A DATE FROM CALENDAR
        incomeDate = root.findViewById(R.id.add_income_date);
        incomeDate.setInputType(InputType.TYPE_NULL);
        incomeDate.setOnClickListener(v -> {
            final Calendar date = Calendar.getInstance();
            int day = date.get(Calendar.DAY_OF_MONTH);
            int month = date.get(Calendar.MONTH);
            int year = date.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(root.getContext(),
                    (view, year1, month1, dayOfMonth) ->
                            incomeDate.setText(String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth)), year, month, day);

            datePicker.show();
        });

        addIncome = root.findViewById(R.id.add_income_btn);
        amount = root.findViewById(R.id.incomeAmount);
        comment = root.findViewById(R.id.commentIncome);

        StatisticsModelClass inputOutgoing = new StatisticsModelClass();

        databaseClass = new DatabaseClass(getContext());

        addIncome.setOnClickListener(v -> {
            System.out.println(isAvailable);
            if (incomeSource.getText().toString().isEmpty()) {
                incomeSource.setError("Indicate the source");
            } else {
                inputOutgoing.setTvType(String.valueOf(incomeSource.getText()));
            }
            if (amount.getText().toString().isEmpty()) {
                amount.setError("Indicate the amount");
            } else {
                inputOutgoing.setTvAmount(Float.parseFloat(String.valueOf(amount.getText())));
            }
            if (incomeTime.getText().toString().isEmpty()) {
                incomeTime.setError("Indicate the time");
            } else {
                inputOutgoing.setTime(String.valueOf(incomeTime.getText()));
            }
            if (incomeDate.getText().toString().isEmpty()) {
                incomeDate.setError("Indicate the date");
            } else {
                inputOutgoing.setDate(String.valueOf(incomeDate.getText()));
            }
            inputOutgoing.setComment(String.valueOf(comment.getText()));
            inputOutgoing.setRepeat(0);

            //insert values into the database if fields arent empty
            if (!(incomeSource.getText().toString().isEmpty() || incomeSource.getText().toString().isEmpty() ||
                    incomeSource.getText().toString().isEmpty() || incomeSource.getText().toString().isEmpty())) {
                String[] date = incomeDate.getText().toString().split("-");
                databaseClass.addIncome(inputOutgoing);
              derivedName = defFilename + monthsMap.get(date[1]) + date[0] + ".csv";

                //TODO Storage granting permission some code issues and will work on that before submission

//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    } else {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                            ActivityCompat.requestPermissions(
//                                    getActivity(),
//                                    new String[]{
//                                            Manifest.permission.READ_EXTERNAL_STORAGE,
//                                            Manifest.permission.MANAGE_EXTERNAL_STORAGE
//                                    },
//                                    1
//                            );
//                            Toast.makeText(getActivity(), "Your data could not be written to drive please\ngrant permissions first", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                } else {
                    CharSequence contentTitle = getString(R.string.app_name);
                    final ProgressDialog progressDialog = ProgressDialog.show(
                            getActivity(), contentTitle, "uploading creating the file",
                            true);
                    DriveRepo driveRepo = new DriveRepo(getActivity(),dsh,progressDialog);
                    dsh.checkFile(derivedName).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    isAvailable = task.getResult();
                                    if (!isAvailable) {
                                        try {
                                            Toast.makeText(getActivity(), "this is a check "+isAvailable, Toast.LENGTH_SHORT).show();
                                            driveRepo.uploadFile(derivedName, inputOutgoing);
                                        } catch (IOException | InterruptedException e) {
                                            e.printStackTrace();
                                            progressDialog.dismiss();
                                        }
                                    } else {
                                        dsh.getFileId(derivedName).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                String fileId = task1.getResult();
                                                dsh.downloadFile(fileId, getActivity()).addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        try {
                                                            driveRepo.updateFile(task2.getResult(), inputOutgoing, fileId);
                                                        } catch (IOException e) {
                                                            progressDialog.dismiss();
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    else {
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                            }
                                            else{
                                                progressDialog.dismiss();
                                            }

                                        });


                                    }
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                }

//            }

        });
        return root;
    }



}
