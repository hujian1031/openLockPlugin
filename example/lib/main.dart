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
    super.initState();
    initPlatformState();
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
//                  print('开始扫描');
//                  OpenLock.startLeScan();
//                  OpenLock.scanResult.listen((v) {
//                    print('结果:${v}');
//                  });
//              OpenLock.connect('D0:CF:5E:A8:3B:3E');
              OpenLock.connect('00:0D:6F:4C:D8:D4');
              await OpenLock.isConnect().then((value) {
                print("连接:${value}");
              });
//
              while (await OpenLock.isConnect() == false) {
                print("未连接成功");
                sleep(Duration(milliseconds: 100));
              }
              print("连接成功!");
//                  await OpenLock.setAdmin("0000000228","2019-11-01 10:04:00");
//                  await OpenLock.setTime("0000000228","2019-11-01 10:04:00");
//              OpenLock.modifyPassowrd("0000000228", "112233");
                  OpenLock.openDoor(
                      "0000000228",
                      "2019-11-18 11:02:00",
                      "2019-11-18 11:02:00",
                      "2019-11-18 11:02:00",
                      "00:00",
                      "23:59",
                      "00:00",
                      "23:59");
//              OpenLock.queryLockDetails("0000000228");
            },
            child: Text('初始化'),
          ),
        ),
      ),
    );
  }
}
