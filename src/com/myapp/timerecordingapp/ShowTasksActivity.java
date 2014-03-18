package com.myapp.timerecordingapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.myapp.model.DataBaseHandler;
import com.myapp.util.Task;

public class ShowTasksActivity extends Activity {
	private DataBaseHandler db;
	private ArrayList<Task> tasksList;
	private ArrayList<String> taskNamesList;
	private ArrayAdapter<String> arrAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_tasks);

		ListView tasksListView = (ListView) findViewById(R.id.show_tasks_listview);
		taskNamesList = new ArrayList<String>();
		arrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, taskNamesList);

		tasksListView.setAdapter(arrAdapter);
		tasksListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				int taskId = tasksList.get(position).getId();
				Intent intent = new Intent("com.myapp.timerecordingapp.TASKDETAILS");
				intent.putExtra("TaskId", taskId);

				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		db = DataBaseHandler.getInstance(getApplicationContext());

		tasksList = db.getAllTasks();
		taskNamesList.clear();
		arrAdapter.clear();

		if (tasksList == null) {
			TextView title = (TextView) findViewById(R.id.show_tasks_title);
			title.setText("You have no tasks created");
		} else {
			for (Task task : tasksList)
				taskNamesList.add(task.getName());

			arrAdapter.notifyDataSetChanged();
		}

		db.close();
	}
}
