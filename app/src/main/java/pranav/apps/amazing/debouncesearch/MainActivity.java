package pranav.apps.amazing.debouncesearch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.List;

import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public class LogAdapter extends ArrayAdapter<String>{

        public LogAdapter(Context context, List<String> logs) {
            super(context, R.layout.item_log,R.id.item_log,logs);
        }
    }
}
