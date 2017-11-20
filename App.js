/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import {
  Platform,
  StyleSheet,
  Text,
  View,
  Button,
  FlatList,
  ScrollView,
} from 'react-native';
import _ from 'lodash';
import BluetoothNative from './BluetoothNative';

export default class App extends Component {
  constructor(props) {
    super(props);
    this.state = {
      greeting: null,
      printerList: [],
    };
  }

  componentWillMount() {
    // BluetoothNative.emitter.addListener('EXAMPLE_EVENT', event => {
    //   this.setState({ greeting: event.greeting });
    // });
  }

  componentWillUnmount() {
    // BluetoothNative.emitter.removeAllListeners();
  }

  _onPressBluetoothList = () => {
    BluetoothNative.bluetoothList().then(data => {
      this.setState({
        printerList: data.map(item => {
          return { key: item };
        }),
      });
      console.log(this.state.printerList);
    });
  };

  _onPressPrintBuilder = () => {
    BluetoothNative.beginDocument();
    BluetoothNative.append('Using string builder\n');
    BluetoothNative.endDocument();
    BluetoothNative.print('BT:00:15:0E:E5:75:DF');
  };

  _onPressPrintBarcode = () => {
    BluetoothNative.beginDocument();
    BluetoothNative.appendBarcode(
      'CT001',
      BluetoothNative.BARCODE_WIDTH_MEDIUM,
      BluetoothNative.BARCODE_DEFAULT_HEIGHT,
      true
    );
    BluetoothNative.endDocument();
    BluetoothNative.print('BT:00:15:0E:E5:75:DF');
  };

  _onPressPrintAlignRight = () => {
    BluetoothNative.beginDocument();
    BluetoothNative.appendWithAlignment(
      'on your right\n',
      BluetoothNative.ALIGN_RIGHT
    );
    BluetoothNative.endDocument();
    BluetoothNative.print('BT:00:15:0E:E5:75:DF');
  };
  render() {
    return (
      <View style={styles.container}>
        <ScrollView>
          <Text style={styles.welcome}>Welcome to React Native Star Print</Text>
          <View style={styles.separator} />
          <FlatList
            data={this.state.printerList}
            renderItem={({ item }) => <Text>{item.key}</Text>}
          />
          <Button
            title="Load printer list from native API"
            onPress={this._onPressBluetoothList}
          />
          <View style={styles.separator} />
          <Button
            title="Print Native append"
            onPress={this._onPressPrintBuilder}
          />
          <View style={styles.separator} />
          <Button
            title="Print Native barcode"
            onPress={this._onPressPrintBarcode}
          />
          <View style={styles.separator} />
          <Button
            title="Print Native align right"
            onPress={this._onPressPrintAlignRight}
          />
        </ScrollView>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
  separator: {
    height: 20,
  },
});
