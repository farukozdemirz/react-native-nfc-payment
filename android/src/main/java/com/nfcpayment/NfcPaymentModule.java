package com.nfcpayment;

import android.nfc.tech.IsoDep;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import java.io.IOException;


@ReactModule(name = NfcPaymentModule.NAME)
public class NfcPaymentModule extends ReactContextBaseJavaModule implements LifecycleEventListener, NfcAdapter.ReaderCallback {
  
  public static final String NAME = "NfcPayment";
  private final ReactApplicationContext reactContext;
  private NfcAdapter mNfcAdapter;

  public NfcPaymentModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    reactContext.addLifecycleEventListener(this);
    mNfcAdapter = NfcAdapter.getDefaultAdapter(reactContext);
    reactContext.addLifecycleEventListener(this);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @Override
  public void onHostResume() {
    if (mNfcAdapter != null) {
      Bundle options = new Bundle();

      options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

      mNfcAdapter.enableReaderMode(reactContext.getCurrentActivity(),
          this,
          NfcAdapter.FLAG_READER_NFC_A |
              NfcAdapter.FLAG_READER_NFC_B |
              NfcAdapter.FLAG_READER_NFC_F |
              NfcAdapter.FLAG_READER_NFC_V |
              NfcAdapter.FLAG_READER_NFC_BARCODE |
              NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
          options);
    }
  }

  @Override
  public void onHostPause() {
     if (mNfcAdapter != null){
        mNfcAdapter.disableReaderMode(reactContext.getCurrentActivity());
     }
  }

  @Override
  public void onHostDestroy() {
    Log.d("NFCACTIONTEST", "onHostDestroy");
  }

  
  @ReactMethod
  public void test(String a, Promise promise) {
    promise.resolve(a);
  }

  @Override
  public void onTagDiscovered(Tag tag) {

    IsoDep isoDep = null;

    try {
      isoDep = IsoDep.get(tag);
      if (isoDep != null) {
        ((Vibrator) reactContext.getCurrentActivity().getSystemService(reactContext.getCurrentActivity().VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(350, 250));
      }

      isoDep.connect();

      try {
        isoDep.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
