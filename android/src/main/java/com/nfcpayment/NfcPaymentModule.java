package com.nfcpayment;

import androidx.annotation.NonNull;

import android.nfc.tech.IsoDep;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

import com.github.devnied.emvnfccard.enums.EmvCardScheme;
import com.github.devnied.emvnfccard.model.Application;
import com.github.devnied.emvnfccard.model.EmvCard;
import com.github.devnied.emvnfccard.parser.EmvTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  private void startNfc(Promise promise) {
    Log.d("NFCACTIONTEST", "startNfc");

    if (mNfcAdapter != null){
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
    if (mNfcAdapter != null){
      mNfcAdapter.disableReaderMode(reactContext.getCurrentActivity());
    }
  }

  @Override
  public void onHostResume() {}


  @ReactMethod
  private void unregisterTagEvent(Promise promise) {
    if (mNfcAdapter != null){
      Log.d("NFCACTIONTEST", "unregisterTagEvent");
      mNfcAdapter.disableReaderMode(this.reactContext.getCurrentActivity());
    }
    promise.resolve(true);
  }

  @Override
  public void onTagDiscovered(Tag tag) {
    IsoDep isoDep = null;
    Log.d("NFCACTIONTEST", "onTagDiscovered");

    try {
      isoDep = IsoDep.get(tag);
      if (isoDep != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          ((Vibrator) reactContext.getCurrentActivity().getSystemService(reactContext.getCurrentActivity().VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(350, 250));
        }
      }

      isoDep.connect();
      NfcPaymentProvider provider = createNfcPaymentProvider(isoDep);
      EmvTemplate parser = createEmvTemplate(provider);
      EmvCard card = parser.readEmvCard();
      CreditCard creditCard = extractCreditCardInfo(card);

      Log.d("NFCACTIONTEST", creditCard.toString());

      try {
        isoDep.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private LocalDate extractExpireDate(Date expireDate) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (expireDate != null) {
        return expireDate.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDate();
      } else {
        return LocalDate.of(1999, 12, 31);
      }
    }
    return null;
  }

  private CreditCard extractCreditCardInfo(EmvCard card) {
    String cardNumber = card.getCardNumber();
    LocalDate expireDate = extractExpireDate(card.getExpireDate());

    EmvCardScheme cardGetType = card.getType();

    CreditCard creditCard = new CreditCard();
    creditCard.setCardNumber(prettyPrintCardNumber(cardNumber));
    creditCard.setExpireDate(expireDate.toString());

    if (cardGetType != null) {
      String typeName = card.getType().getName();
      String[] typeAids = card.getType().getAid();
      creditCard.setCardType(typeName);
      creditCard.setTypeAids(typeAids);
    }

    List<Application> applications = card.getApplications();
    creditCard.setApplications(applications);

    return creditCard;
  }

  private EmvTemplate createEmvTemplate(NfcPaymentProvider provider) {
    EmvTemplate.Config config = EmvTemplate.Config()
      .setContactLess(true)
      .setReadAllAids(true)
      .setReadTransactions(true)
      .setRemoveDefaultParsers(false)
      .setReadAt(true);

    return EmvTemplate.Builder()
      .setProvider(provider)
      .setConfig(config)
      .build();
  }

  private NfcPaymentProvider createNfcPaymentProvider(IsoDep isoDep) {
    NfcPaymentProvider provider = new NfcPaymentProvider();
    provider.setmTagCom(isoDep);
    return provider;
  }

  public static String prettyPrintCardNumber(String cardNumber) {
      if (cardNumber == null) return null;
      char delimiter = ' ';
      return cardNumber.replaceAll(".{4}(?!$)", "$0" + delimiter);
  }
}
