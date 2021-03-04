package taskboard.androidclient.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.util.List;

import taskboard.androidclient.R;
import taskboard.androidclient.data.Task;
import taskboard.androidclient.data.TaskBoardAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import taskboard.androidclient.data.TaskReponse;

public class ActivityTasks extends AppCompatActivity {
    private static final int REQUEST_CODE_CREATE_TASK = 1001;
    private TextView textViewStatus;
    private String apiBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        this.apiBaseUrl = this.getIntent().getStringExtra("paramApiBaseUrl");
        if (!this.apiBaseUrl.endsWith("/"))
            this.apiBaseUrl += "/";

        this.textViewStatus = findViewById(R.id.textViewStatus);

        EditText editTextKeyword = findViewById(R.id.editTextKeyword);
        editTextKeyword.requestFocus();

        Button buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(v -> {
            hideSoftKeyboard(this);
            String keyword = editTextKeyword.getText().toString();
            searchTasksByKeyword(keyword);
        });

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(v -> {
            hideSoftKeyboard(this);
            Intent intent = new Intent(this, AddTaskActivity.class);
            startActivityForResult(intent, REQUEST_CODE_CREATE_TASK);
        });

        Button buttonReload = findViewById(R.id.buttonReload);
        buttonReload.setOnClickListener(v -> {
            hideSoftKeyboard(this);
            editTextKeyword.setText("");
            searchTasksByKeyword("");
        });

        searchTasksByKeyword("");
    }

    private void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager)activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private void searchTasksByKeyword(String keyword) {
        showStatusMsg("Loading tasks ...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TaskBoardAPI service = retrofit.create(TaskBoardAPI.class);

        Call<List<Task>> request;
        if (keyword.length() > 0)
            request = service.findContactsByKeyword(keyword);
        else
            request = service.getTasks();
        request.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.code() != HttpURLConnection.HTTP_OK) {
                    showErrorMsg("HTTP code: " + response.code());
                    return;
                }
                displayTasks(response.body());
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                showErrorMsg(t.getMessage());
            }
        });
    }

    private void displayTasks(List<Task> tasks) {
        showSuccessMsg("Tasks found: " + tasks.size());

        // Lookup the recyclerview in activity layout
        RecyclerView recyclerViewTasks =
                (RecyclerView) findViewById(R.id.recyclerViewTasks);

        TasksAdapter tasksAdapter = new TasksAdapter(tasks);
        // Attach the adapter to the RecyclerView to populate items
        recyclerViewTasks.setAdapter(tasksAdapter);
        // Set layout manager to position the items
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_TASK && resultCode == RESULT_OK) {
            String title = data.getStringExtra("title");
            String description = data.getStringExtra("description");
            CreateNewTask(title, description);
        }
    }

    private void CreateNewTask(String title, String description) {
        showStatusMsg("Creating new task ...");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.apiBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TaskBoardAPI service = retrofit.create(TaskBoardAPI.class);

        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        Call<TaskReponse> request = service.create(task);
        request.enqueue(new Callback<TaskReponse>() {
            @Override
            public void onResponse(Call<TaskReponse> call, Response<TaskReponse> response) {
                if (response.code() != HttpURLConnection.HTTP_CREATED) {
                    showErrorMsg("HTTP code: " + response.code());
                    return;
                }
                showSuccessMsg("Task #" + response.body().getTask().getId() + " created.");
            }

            @Override
            public void onFailure(Call<TaskReponse> call, Throwable t) {
                showErrorMsg(t.getMessage());
            }
        });
    }

    private void showStatusMsg(String msg) {
        textViewStatus.setText(msg);
        textViewStatus.setBackgroundResource(R.color.backgroundStatus);
    }

    private void showSuccessMsg(String msg) {
        textViewStatus.setText(msg);
        textViewStatus.setBackgroundResource(R.color.backgroundSuccess);
    }

    private void showErrorMsg(String errMsg) {
        textViewStatus.setText(errMsg);
        textViewStatus.setBackgroundResource(R.color.backgroundError);
    }
}
