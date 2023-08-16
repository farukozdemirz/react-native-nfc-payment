
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNNfcPaymentSpec.h"

@interface NfcPayment : NSObject <NativeNfcPaymentSpec>
#else
#import <React/RCTBridgeModule.h>

@interface NfcPayment : NSObject <RCTBridgeModule>
#endif

@end
