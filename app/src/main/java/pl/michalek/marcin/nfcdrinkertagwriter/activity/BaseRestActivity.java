/**
 * Created by Marcin Michałek on 2015-01-02.
 *
 */
package pl.michalek.marcin.nfcdrinkertagwriter.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * TODO Add class description...
 *
 * @author Marcin Michałek
 */
public class BaseRestActivity extends AppCompatActivity {
  public SpiceManager getSpiceManager() {
    return spiceManager;
  }

  protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
  protected ProgressDialog progressDialog;

  @Override
  protected void onStart() {
    super.onStart();
    spiceManager.start(this);
  }

  @Override
  protected void onStop() {
    spiceManager.shouldStop();
    super.onStop();
  }

  protected void showProgressDialog(String title, String message){
    progressDialog = ProgressDialog.show(this, title, message);
  }

  protected void hideProgressDialog(){
    progressDialog.dismiss();
  }
}
