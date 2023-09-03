# react-native-nfc-payment

A React Native module that allows you to receive payments with nfc.

This package will only work on Android and isn't available for iOS as of 2020 because Apple do not allow 3rd party iPhone apps to use the Core NFC framework.

## Installation

```sh
npm install react-native-nfc-payment
```

### Android

Simple add `uses-permission` into your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.NFC" />
<uses-permission android:name="android.permission.VIBRATE" />
```

### Methods

| Method               | Description                     |
| -------------------- | ------------------------------- |
| `registerTagEvent`   | `Start the nfc reading process` |
| `unregisterTagEvent` | `Close the nfc reading process` |

### Options

| Method                 | Description                                        | Default |
| ---------------------- | -------------------------------------------------- | ------- |
| `contactLess`          | `Enable contact less reading`                      | `true`  |
| `readAllAids`          | `Read all aids in card`                            | `true`  |
| `readTransactions`     | `Read all transactions`                            | `true`  |
| `removeDefaultParsers` | `Remove default parsers for GeldKarte and EmvCard` | `false`  |
| `readAt`               | `Read and extract ATR/ATS and description`         | `true`  |

## Usage / Example

```javascript
import { useEffect, useState } from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import {
  registerTagEvent,
  unregisterTagEvent,
  type INfcModuleConfig,
  type INfcCardInfo,
} from "react-native-nfc-payment";

export default function App() {
  const [cardInfos, setCardInfos] = useState<INfcCardInfo | null>(null);

  useEffect(() => {
    onRegisterTagEvent();
    return () => {
      onUnregisterTagEvent();
    };
  }, []);

  const onRegisterTagEvent = async () => {
    try {
      var options: INfcModuleConfig = {
        contactLess: true,
        readAllAids: true,
        readTransactions: true,
        removeDefaultParsers: false,
        readAt: true,
      };
      const result = await registerTagEvent(options);
      console.log("unregisterTagEvent::result", JSON.parse(result));
    } catch (error) {
      console.log("error", error);
    }
  };

  const onUnregisterTagEvent = async () => {
    try {
      const result = await unregisterTagEvent();
      console.log("unregisterTagEvent::result", result);
    } catch (error) {
      console.log("error", error);
    }
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity style={styles.buttonStyle} onPress={onRegisterTagEvent}>
        <Text>Register Tag Event</Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.buttonStyle} onPress={onUnregisterTagEvent}>
        <Text>Unregister Tag Event</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    paddingHorizontal: 30,
  },
  buttonStyle: {
    padding: 10,
    backgroundColor: "#ddd",
    borderRadius: 40,
    alignItems: "center",
    marginBottom: 20,
  },
});

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## Acknowledgements
Thanks to the authors of these libraries for inspiration:



- Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
- [EMV-NFC-Paycard-Enrollment](https://github.com/devnied/EMV-NFC-Paycard-Enrollment)


## License

MIT

---
