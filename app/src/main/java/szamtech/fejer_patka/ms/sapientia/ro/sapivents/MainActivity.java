package szamtech.fejer_patka.ms.sapientia.ro.sapivents;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    //Modification for dev branch
    @BindView(R.id.example_button) Button exampleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.example_button) void exampleOnClick(){
        Toast toast =
                Toast.makeText(getApplicationContext(), "Example text", Toast.LENGTH_LONG);
        toast.show();
    }
}
