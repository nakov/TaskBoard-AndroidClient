package taskboard.androidclient.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import taskboard.androidclient.R;

public class AddTaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        editTextTitle.requestFocus();
        EditText editTextDescription = findViewById(R.id.editTextDescription);

        Button buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        Button buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(v -> {
            Intent resultData = new Intent();
            resultData.putExtra("title", editTextTitle.getText().toString());
            resultData.putExtra("description", editTextDescription.getText().toString());
            setResult(RESULT_OK, resultData);
            finish();
        });
    }
}