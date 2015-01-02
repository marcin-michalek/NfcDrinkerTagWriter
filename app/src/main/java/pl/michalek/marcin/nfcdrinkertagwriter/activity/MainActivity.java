package pl.michalek.marcin.nfcdrinkertagwriter.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.gson.Gson;
import pl.michalek.marcin.nfcdrinkertagwriter.R;
import pl.michalek.marcin.nfcdrinkertagwriter.network.BaseNonContextRequestListener;
import pl.michalek.marcin.nfcdrinkertagwriter.network.request.AlcoholKindRequest;
import pl.michalek.marcin.nfcdrinkertagwriter.network.response.StringListResponse;
import pl.michalek.marcin.nfcdrinkertagwriter.nfc.model.Drinker;

import java.io.IOException;

import static android.nfc.NdefRecord.createMime;


public class MainActivity extends BaseRestActivity {
  @InjectView(R.id.alcoholKindSpinner)
  Spinner alcoholKindSpinner;
 NfcAdapter nfcAdapter;

  PendingIntent pendingIntent;

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

  @OnClick(R.id.writeTagButton)
  void writeToTag() {

  }

  private Drinker createdDrinker() {
    Drinker drinker = new Drinker();
    drinker.setName(((EditText) findViewById(R.id.nameEditText)).getText().toString());
    drinker.setWeight(Double.parseDouble(((EditText) findViewById(R.id.weightEditText)).getText().toString()));
    drinker.setHeight(Double.parseDouble(((EditText) findViewById(R.id.heightEditText)).getText().toString()));
    drinker.setAlcoholKind(((Spinner) findViewById(R.id.alcoholKindSpinner)).getSelectedItem().toString());
    drinker.setAlcoholVoltage(Double.parseDouble(((EditText) findViewById(R.id.alcoholVoltageEditText)).getText().toString()));
    drinker.setGender(((Spinner) findViewById(R.id.genderSpinner)).getSelectedItem().toString());
    drinker.setStomach(((Spinner) findViewById(R.id.stomachStateSpinner)).getSelectedItem().toString());
    return drinker;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    NdefMessage ndefMessage = createNdefMessage();
    writeTag(ndefMessage, tag);

  }

  public void writeTag(NdefMessage message, Tag tag) {
      int size = message.toByteArray().length;
      String mess = "";
      try {
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
          ndef.connect();

          ndef.writeNdefMessage(message);

        } else {
          NdefFormatable format = NdefFormatable.get(tag);
          if (format != null) {
            try {
              format.connect();
              format.format(message);
              mess = "Formatted tag and wrote message";
            } catch (IOException e) {
              mess = "Failed to format tag.";
            }
          } else {
            mess = "Tag doesn't support NDEF.";
          }
        }
      } catch (Exception e) {
        mess = "Failed to write tag";
      }
    }

  public NdefMessage createNdefMessage() {
    return new NdefMessage(
        new NdefRecord[]{
            createMime(
                "application/pl.michalek.marcin.nfcdrinkertagwriter", new Gson().toJson(createdDrinker()).getBytes())
            , NdefRecord.createApplicationRecord("pl.michalek.marcin.nfcdrinkerstation")
        });
  }
}
