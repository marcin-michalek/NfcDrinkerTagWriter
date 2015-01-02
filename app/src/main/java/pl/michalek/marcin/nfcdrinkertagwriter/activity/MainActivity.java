package pl.michalek.marcin.nfcdrinkertagwriter.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import pl.michalek.marcin.nfcdrinkertagwriter.R;
import pl.michalek.marcin.nfcdrinkertagwriter.network.BaseNonContextRequestListener;
import pl.michalek.marcin.nfcdrinkertagwriter.network.request.AlcoholKindRequest;
import pl.michalek.marcin.nfcdrinkertagwriter.network.response.StringListResponse;


public class MainActivity extends BaseRestActivity {
  @InjectView(R.id.alcoholKindSpinner)
  Spinner alcoholKindSpinner;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    downloadAlcoholKinds();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void downloadAlcoholKinds() {
    getSpiceManager().execute(new AlcoholKindRequest(), new BaseNonContextRequestListener<StringListResponse>() {
      @Override
      public void onRequestSuccess(StringListResponse alcoholKindList) {
        alcoholKindSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, alcoholKindList));
      }
    });
  }
}
