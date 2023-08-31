import { StyleSheet, Text, View } from 'react-native';
import React from 'react';
import type { INfcCardInfo } from 'react-native-nfc-payment';

const CardInfo = ({
  cardInfos,
  index,
}: {
  cardInfos: INfcCardInfo;
  index: number;
}) => {
  const cardApplication = cardInfos?.applications
    ? cardInfos.applications[index]
    : null;

  return (
    <View style={styles.cardInfos}>
      <Text style={styles.textStyle}>Card Number: {cardInfos.cardNumber}</Text>
      <Text style={styles.textStyle}>Card Type: {cardInfos.cardType}</Text>
      <Text style={styles.textStyle}>
        Card Expire Date: {cardInfos.expireDate}
      </Text>
      <View style={styles.row}>
        <Text style={styles.textStyle}>
          Application Label: {cardApplication?.applicationLabel}
        </Text>
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
