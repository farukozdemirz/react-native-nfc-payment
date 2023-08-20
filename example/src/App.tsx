import * as React from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { registerTagEvent, unregisterTagEvent } from 'react-native-nfc-payment';

export default function App() {
  const onRegisterTagEvent = async () => {
    try {
      var options = {};
      const result = await registerTagEvent(options);
      console.log('registerTagEvent::result', result);
    } catch (error) {
      console.log('error', error);
    }
  };

  const onPauseNfc = async () => {
    try {
      const result = await unregisterTagEvent();
      console.log('unregisterTagEvent::result', result);
    } catch (error) {
      console.log('error', error);
    }
  };

  return (
    <View style={styles.container}>
      <TouchableOpacity style={styles.buttonStyle} onPress={onRegisterTagEvent}>
        <Text>registerTagEvent NFC</Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.buttonStyle} onPress={onPauseNfc}>
        <Text>unregisterTagEvent NFC</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },

  buttonStyle: {
    margin: 20,
    padding: 10,
    backgroundColor: '#ddd',
    borderRadius: 40,
  },
});
