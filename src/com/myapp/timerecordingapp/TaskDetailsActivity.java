package com.myapp.timerecordingapp;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
}
