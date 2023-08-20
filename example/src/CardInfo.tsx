import { StyleSheet, Text, View } from 'react-native';
import React from 'react';
import type { INfcCardInfo } from 'react-native-nfc-payment';

const CardInfo = ({ cardInfos }: { cardInfos: INfcCardInfo }) => {
  return (
    <View style={styles.cardInfos}>
      <Text style={styles.textStyle}>Card Number: {cardInfos.cardNumber}</Text>
      <Text style={styles.textStyle}>Card Type: {cardInfos.cardType}</Text>
      <Text style={styles.textStyle}>
        Card Expire Date: {cardInfos.expireDate}
      </Text>
      <View style={styles.row}>
        <Text style={styles.textStyle}>Type Aids:{` [ `}</Text>
        {cardInfos.typeAids?.length > 0 &&
          cardInfos.typeAids.map((aid, index) => (
            <Text key={index} style={styles.textStyle}>
              {aid}
              {index < cardInfos.typeAids.length - 1 && ', '}
            </Text>
          ))}
        <Text style={styles.textStyle}>{` ]`}</Text>
      </View>
    </View>
  );
};

export default CardInfo;

const styles = StyleSheet.create({
  cardInfos: {
    marginBottom: 30,
  },
  textStyle: {
    marginBottom: 10,
    fontWeight: '600',
  },
  row: {
    flexDirection: 'row',
  },
});
