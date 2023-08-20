import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-nfc-payment' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const NfcPayment = NativeModules.NfcPayment
  ? NativeModules.NfcPayment
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function registerTagEvent(options: object): Promise<String> {
  return NfcPayment.registerTagEvent(options);
}

export function unregisterTagEvent(): Promise<Boolean> {
  return NfcPayment.unregisterTagEvent();
}
