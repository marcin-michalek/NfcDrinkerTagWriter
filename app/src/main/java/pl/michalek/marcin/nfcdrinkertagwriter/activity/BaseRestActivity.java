/**
 * Created by Marcin Michałek on 2015-01-02.
 *
 */
package pl.michalek.marcin.nfcdrinkertagwriter.activity;

import android.app.Activity;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * TODO Add class description...
 *
 * @author Marcin Michałek
 */
public class BaseRestActivity extends Activity {
  public SpiceManager getSpiceManager() {
    return spiceManager;
  }

  protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

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

}
