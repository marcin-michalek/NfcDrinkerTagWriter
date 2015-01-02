/**
 * Created by Marcin Michałek on 2015-01-02.
 *
 */
package pl.michalek.marcin.nfcdrinkertagwriter.network;

import android.util.Log;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import pl.michalek.marcin.nfcdrinkertagwriter.config.Constants;

/**
 * TODO Add class description...
 *
 * @author Marcin Michałek
 */
public abstract class BaseNonContextRequestListener<T> implements RequestListener<T> {

  @Override
  public void onRequestFailure(SpiceException spiceException) {
    Log.e(Constants.LOGTAG, spiceException.getMessage(), spiceException);
  }

  @Override
  public abstract void onRequestSuccess(T t);
}
