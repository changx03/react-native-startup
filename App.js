/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 * @flow
 */

import React, { Component } from 'react';
import { Platform, StyleSheet, Text, View, Button } from 'react-native';
import BluetoothNative from './BluetoothNative';

export default class App extends Component {
  state = {
    greeting: null
  };

  componentWillMount() {
    BluetoothNative
      .emitter
      .addListener('EXAMPLE_EVENT', event => {
        console.log(event);
        this.setState({ greeting: event.greeting });
      });
  }

  componentWillUnmount() {
    BluetoothNative
      .emitter
      .removeAllListeners();
  }

  _onPress = () => {
    BluetoothNative.exampleMethod();
  };

  _onPreseeBluetoothList = () => {
    BluetoothNative.bluetoothList();
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to React Native!</Text>
        <Text>{BluetoothNative.EXAMPLE_CONSTANT}</Text>
        <Text style={styles.instructions}>{this.state.greeting}</Text>
        <Button title="Load native string" onPress={this._onPress} />
        <Button title="Bluetooth list" onPress={this._onPreseeBluetoothList} />
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF'
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5
  }
});
