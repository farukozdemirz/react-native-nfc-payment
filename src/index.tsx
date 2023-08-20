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

export interface ICardTransaction {
  amount: number;
  currency: string;
  cyptogramData: string;
  date: string;
  time: string;
}

export interface ICardApplication {
  aid: number[];
  amount: number;
  applicationLabel: string;
  leftPinTry: number;
  listTransactions: ICardTransaction[];
  priority: number;
  readingStep: string;
  transactionCounter: number;
}

export interface INfcCardInfo {
  cardNumber: string;
  cardType: string;
  expireDate: string;
  typeAids: string[];
  applications: ICardApplication[];
}

export interface INfcModuleConfig {
  contactLess?: boolean;
  readAllAids?: boolean;
  readTransactions?: boolean;
  removeDefaultParsers?: boolean;
  readAt?: boolean;
}

export function registerTagEvent(config: INfcModuleConfig): Promise<string> {
  return NfcPayment.registerTagEvent(config);
}

export function unregisterTagEvent(): Promise<boolean> {
  return NfcPayment.unregisterTagEvent();
}
