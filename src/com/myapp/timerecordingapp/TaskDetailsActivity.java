package com.myapp.timerecordingapp;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.model.DataBaseHandler;
import com.myapp.util.Record;
import com.myapp.util.Wifi;

public class TaskDetailsActivity extends Activity {
	private ArrayList<Record> records;
	private ArrayList<String> recordsFormatListView;
	private ArrayAdapter<String> arrAdapter;
	private TextView textView;
	private int taskId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_details);

		recordsFormatListView = new ArrayList<String>();
		arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, recordsFormatListView);

		textView = (TextView) findViewById(R.id.show_rec_title);
		ListView recListView = (ListView) findViewById(R.id.show_records);

		recListView.setAdapter(arrAdapter);
		recListView.setClickable(true);

		Bundle extras = getIntent().getExtras();
		taskId = extras.getInt("TaskId");

		Button buttonRemoveTasks = (Button) findViewById(R.id.button_remove_task);
		buttonRemoveTasks.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				alertMessage();
			}
		});

		Button buttonSendEmail = (Button) findViewById(R.id.button_send_email);
		SendMailOnClickListener sendMailListener = new SendMailOnClickListener(taskId,
				recordsFormatListView, getApplicationContext());
		buttonSendEmail.setOnClickListener(sendMailListener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		DataBaseHandler db = DataBaseHandler.getInstance(getApplicationContext());
		records = db.getRecords(taskId);

		if (records == null) {
			textView.setText("No records to show");
		} else {
			textView.setText("Records from Task  '"+ db.getTaskName(taskId) + "'" + ", Wifi: '" + db.getWifi(taskId).getSsid() + "'");

			recordsFormatListView.clear();
			for (Record rec : records)
				recordsFormatListView.add("ChIn: " + rec.getCheckIn() + "\nChOut: " + rec.getCheckOut());

			arrAdapter.notifyDataSetChanged();
		}

		db.close();
	}

	public void alertMessage() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which ==  DialogInterface.BUTTON_POSITIVE) {
					DataBaseHandler db = DataBaseHandler.getInstance(getApplicationContext());
					db.removeTask(taskId);
					db.close();
					finish();
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure?")
		.setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener)
		.show();
	}
}
