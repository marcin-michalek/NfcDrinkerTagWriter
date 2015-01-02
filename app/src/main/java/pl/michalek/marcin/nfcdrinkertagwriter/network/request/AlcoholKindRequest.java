/**
 * Created by Marcin Michałek on 2015-01-02.
 *
 */
package pl.michalek.marcin.nfcdrinkertagwriter.network.request;


import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;
import pl.michalek.marcin.nfcdrinkertagwriter.network.ServicePahts;
import pl.michalek.marcin.nfcdrinkertagwriter.network.response.StringListResponse;

/**
 * TODO Add class description...
 *
 * @author Marcin Michałek
 */
public class AlcoholKindRequest extends SpringAndroidSpiceRequest<StringListResponse> {
  public AlcoholKindRequest() {
    super(StringListResponse.class);
  }

  @Override
  public StringListResponse loadDataFromNetwork() throws Exception {
    return getRestTemplate().getForObject(ServicePahts.ALCOHOL_KIND_URL, StringListResponse.class);
  }
}
