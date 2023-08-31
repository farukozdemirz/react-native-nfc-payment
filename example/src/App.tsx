import React, { useEffect, useState } from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import {
  registerTagEvent,
  unregisterTagEvent,
  type INfcModuleConfig,
  type INfcCardInfo,
} from 'react-native-nfc-payment';
import CardInfo from './CardInfo';

export default function App() {
  const [cardInfos, setCardInfos] = useState<INfcCardInfo[] | null>(null);

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
      setCardInfos(JSON.parse(result));
    } catch (error) {
      console.log('error', error);
    }
  };

  const onUnregisterTagEvent = async () => {
    try {
      const result = await unregisterTagEvent();
      console.log('unregisterTagEvent::result', result);
    } catch (error) {
      console.log('error', error);
    }
  };
  return (
    <View style={styles.container}>
      {cardInfos &&
        cardInfos.map((card, index) => (
          <CardInfo key={index} index={index} cardInfos={card} />
        ))}
      <TouchableOpacity style={styles.buttonStyle} onPress={onRegisterTagEvent}>
        <Text>Register Tag Event</Text>
      </TouchableOpacity>
      <TouchableOpacity
        style={styles.buttonStyle}
        onPress={onUnregisterTagEvent}
      >
        <Text>Unregister Tag Event</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    paddingHorizontal: 30,
  },
  buttonStyle: {
    padding: 10,
    backgroundColor: '#ddd',
    borderRadius: 40,
    alignItems: 'center',
    marginBottom: 20,
  },
});
