/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import React from 'react';
import type {PropsWithChildren} from 'react';
import {
  SafeAreaView,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  NativeModules,
  useColorScheme,
  View,
  Pressable,
} from 'react-native';

import {
  Colors,
  DebugInstructions,
  Header,
  LearnMoreLinks,
  ReloadInstructions,
} from 'react-native/Libraries/NewAppScreen';
import uuid from 'react-native-uuid';
import axios from 'axios';
import {API_GET_IDENT, API_LOGIN_URL, IDNOW_KEY, COMPANY} from '@env';

type SectionProps = PropsWithChildren<{
  title: string;
}>;

const {IDNow} = NativeModules;

function Section({children, title}: SectionProps): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';
  return (
    <View style={styles.sectionContainer}>
      <Text
        style={[
          styles.sectionTitle,
          {
            color: isDarkMode ? Colors.white : Colors.black,
          },
        ]}>
        {title}
      </Text>
      <Text
        style={[
          styles.sectionDescription,
          {
            color: isDarkMode ? Colors.light : Colors.dark,
          },
        ]}>
        {children}
      </Text>
    </View>
  );
}

const startIDNOW = async () => {
  let token = uuid.v4();
  let loginToken = '';
  let processPending = '';
  let processId = '';
  const userData = {
    email: 'something@gmail.com',
    firstname: 'test',
    lastname: 'test',
    gender: 'MALE',
  };

  try {
    const {data} = await axios.post(API_LOGIN_URL, {
      apiKey: IDNOW_KEY,
    });
    console.log('authToken', data.authToken);
    loginToken = data.authToken;
  } catch (error) {
    return console.log('ERROR login:   ', error);
  }

  try {
    const {data} = await axios.post(
      API_GET_IDENT + token + '/start',
      userData,
      {
        headers: {
          Accept: 'application/json',
          'X-API-LOGIN-TOKEN': loginToken,
          'Content-Type': 'application/json',
        },
      },
    );
    processId = data.id;
    await IDNow.show(
      data.id,
      COMPANY,
      (errorMessage: any) => {
        console.log('ERROR:   ', errorMessage);
      },
      (result: any) => {
        if (result && result.status === 'success') {
          console.log('SUCCESS:   ', result);
        }
      },
    );
  } catch (error) {
    return console.log('ERROR start ident:   ', error);
  }
};

function App(): JSX.Element {
  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
  };

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar
        barStyle={isDarkMode ? 'light-content' : 'dark-content'}
        backgroundColor={backgroundStyle.backgroundColor}
      />
      <ScrollView
        contentInsetAdjustmentBehavior="automatic"
        style={backgroundStyle}>
        <Header />
        <View
          style={{
            backgroundColor: isDarkMode ? Colors.black : Colors.white,
          }}>
          <Pressable
            style={{width: '100%', height: '100%', alignItems: 'center'}}
            onPress={startIDNOW}>
            <Text style={{fontSize: 20}}>Press me</Text>
          </Pressable>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  sectionContainer: {
    marginTop: 32,
    paddingHorizontal: 24,
  },
  sectionTitle: {
    fontSize: 24,
    fontWeight: '600',
  },
  sectionDescription: {
    marginTop: 8,
    fontSize: 18,
    fontWeight: '400',
  },
  highlight: {
    fontWeight: '700',
  },
});

export default App;
