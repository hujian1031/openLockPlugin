import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:open_lock/open_lock.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    initPlatformState();
    super.initState();
  }

  Future<void> initPlatformState() async {
    await OpenLock.init();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: RaisedButton(
            onPressed: () async {
              bool isOpen;
              print('开始');
              isOpen = await OpenLock.isBluetoothOpen();
              print('蓝牙状态:${isOpen}');
              if (!isOpen) {
                print('蓝牙未打开,开启蓝牙');
                await OpenLock.openBluetooth();
              }
//              print('开始扫描');
//              OpenLock.startLeScan();
//              OpenLock.scanResult.listen((v) {
//                print('结果:${v}');
//              });
              OpenLock.connectState.listen((connectState) {
                print("连接状态:$connectState");

                if(connectState['connectState']=='success'){
                  print("连接成功!");
                  OpenLock.sendCmdState.listen((v) {
                    print("开锁状态：$v");
                  });
                  OpenLock.onUnlockState.listen((v){
                    print("开锁状态：$v");
                  });
                  OpenLock.openDoor(
                      "0000000228",
                      "2019-11-19 10:27:00",
                      "2019-11-01 00:00:00",
                      "2019-12-31 00:00:00",
                      "00:00",
                      "23:59",
                      "00:00",
                      "23:59");
                }


              });
              OpenLock.connect('D0:CF:5E:A8:3B:3E');

            },
            child: Text('初始化'),
          ),
        ),
      ),
    );
  }
}
