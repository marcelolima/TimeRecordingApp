package com.myapp.timerecordingapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.model.DataBaseHandler;
import com.myapp.util.Task;
import com.myapp.util.Wifi;

public class NewTaskActivity extends Activity {
	private static final String TAG = NewTaskActivity.class.getSimpleName();

	private String ssid = null, bssid = null;
	private String newTaskName, newTaskDesc = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newtask);

		Button b_create = (Button) findViewById(R.id.button_create);
		Button b_scanWifi = (Button) findViewById(R.id.button_select_wifi);

		b_scanWifi.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.myapp.timerecordingapp.SELECTWIFI");
				startActivityForResult(intent, Wifi.CHOOSE_WIFI);
			}
		});

		b_create.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				newTaskName = ((EditText) findViewById(R.id.taskName)).getText().toString();
				if (newTaskName.length() == 0) {
					Toast toast = Toast.makeText(getApplicationContext(), "Specify the task name", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}

				if (ssid == null) {
					Toast toast = Toast.makeText(getApplicationContext(), "You must select a Wifi", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}

				DataBaseHandler db = DataBaseHandler.getInstance(getApplicationContext());

				newTaskDesc = ((EditText) findViewById(R.id.taskDesc)).getText().toString();
				Task newTask = new Task(newTaskName, newTaskDesc, ssid, bssid);

				int idTask = (int) db.addTask(newTask);
				if (idTask == -1) {
					Toast toast = Toast.makeText(getApplicationContext(), "Task name already exists", Toast.LENGTH_SHORT);
					toast.show();
					return;
				}

				db.close();
				finish();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if (requestCode == Wifi.CHOOSE_WIFI && resultCode == RESULT_OK) {
			bssid = data.getStringExtra(Wifi.KEY_BSSID);
			ssid = data.getStringExtra(Wifi.KEY_SSID);
			TextView textView = (TextView) findViewById(R.id.textViewNetworks);
			textView.setText(ssid);
		}
	}
}
