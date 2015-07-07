package pl.michalek.marcin.nfcdrinkertagwriter.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.google.gson.Gson;
import pl.michalek.marcin.nfcdrinkertagwriter.R;
import pl.michalek.marcin.nfcdrinkertagwriter.config.Constants;
import pl.michalek.marcin.nfcdrinkertagwriter.network.BaseNonContextRequestListener;
import pl.michalek.marcin.nfcdrinkertagwriter.network.request.AlcoholKindRequest;
import pl.michalek.marcin.nfcdrinkertagwriter.network.response.StringListResponse;
import pl.michalek.marcin.nfcdrinkertagwriter.nfc.model.Drinker;

import java.io.IOException;

import static android.nfc.NdefRecord.createMime;


public class MainActivity extends BaseRestActivity {
  @InjectView(R.id.alcoholKindSpinner)
  Spinner alcoholKindSpinner;

  private NfcAdapter nfcAdapter;

  private PendingIntent pendingIntent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.inject(this);
    downloadAlcoholKinds();
    nfcAdapter = NfcAdapter.getDefaultAdapter(this);

    pendingIntent = PendingIntent.getActivity(
        this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
  }

  @Override
  protected void onResume() {
    super.onResume();
    nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
  }

  @Override
  protected void onPause() {
    nfcAdapter.disableForegroundDispatch(this);
    super.onPause();
  }

  private void downloadAlcoholKinds() {
    showProgressDialog("", getString(R.string.downloading));
    getSpiceManager().execute(new AlcoholKindRequest(), new BaseNonContextRequestListener<StringListResponse>() {
      @Override
      public void onRequestSuccess(StringListResponse alcoholKindList) {
        hideProgressDialog();
        alcoholKindSpinner.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, alcoholKindList));
      }
    });
  }

  private Drinker createdDrinkerFromUserInput() {
    Drinker drinker = new Drinker();
    drinker.setName(((EditText) findViewById(R.id.nameEditText)).getText().toString());
    drinker.setWeight(Double.parseDouble(((EditText) findViewById(R.id.weightEditText)).getText().toString()));
    drinker.setHeight(Double.parseDouble(((EditText) findViewById(R.id.heightEditText)).getText().toString()));
    drinker.setAlcoholKind(((Spinner) findViewById(R.id.alcoholKindSpinner)).getSelectedItem().toString());
    drinker.setAlcoholVoltage(Double.parseDouble(((EditText) findViewById(R.id.alcoholVoltageEditText)).getText().toString()));
    drinker.setGender(((Spinner) findViewById(R.id.genderSpinner)).getSelectedItem().toString());
    drinker.setStomach(((Spinner) findViewById(R.id.stomachStateSpinner)).getSelectedItem().toString());
    drinker.setShotCapacity(Double.parseDouble(((EditText) findViewById(R.id.shotCapacityEditText)).getText().toString()));
    return drinker;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    NdefMessage ndefMessage = createNdefMessage();
    writeTag(ndefMessage, tag);
  }

  public void writeTag(NdefMessage ndefMessage, Tag tag) {
    try {
      Ndef ndef = Ndef.get(tag);
      if (ndef != null) {
        ndef.connect();
        ndef.writeNdefMessage(ndefMessage);
      } else {
        NdefFormatable format = NdefFormatable.get(tag);
        if (format != null) {
          try {
            format.connect();
            format.format(ndefMessage);
          } catch (IOException e) {
            Log.e(Constants.LOGTAG, e.getMessage(), e);
          }
        }
      }
    } catch (Exception e) {
      Log.e(Constants.LOGTAG, e.getMessage(), e);
    }
  }

  public NdefMessage createNdefMessage() {
    return new NdefMessage(
        new NdefRecord[]{
            createMime(
                Constants.TAG_WRITER_MIME_TYPE, new Gson().toJson(createdDrinkerFromUserInput()).getBytes())
            , NdefRecord.createApplicationRecord(Constants.DRINKER_STATION_APP_RECORD)
        });
  }
}
