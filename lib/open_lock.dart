import 'dart:async';

import 'package:flutter/services.dart';

class OpenLock {
  static const MethodChannel _channel = const MethodChannel('open_lock');

  //扫描设备流
  static StreamController<String> _scanResultListenerStreamController =
      new StreamController.broadcast();

  static Stream<String> get scanResult =>
      _scanResultListenerStreamController.stream;

  //连接失败流
  static StreamController<String> _connectFailListenerStreamController =
      new StreamController.broadcast();

  static Stream<String> get connectResult =>
      _connectFailListenerStreamController.stream;

  //设备关闭
  static StreamController<bool> _deviceCloseListenerStreamController =
      new StreamController.broadcast();

  static Stream<bool> get deviceClose =>
      _deviceCloseListenerStreamController.stream;

  //java调flutter方法处理器
  static Future<dynamic> handler(MethodCall call) {
    String method = call.method;
    switch (method) {
      case "scanListener":
        {
          _scanResultListenerStreamController.add(call.arguments);
          break;
        }
      case "connectFailListener":
        {
          print("connectFailListener输出的信息了");
          _connectFailListenerStreamController.add(call.arguments);
          break;
        }
      case "deviceClose":
        {
          _deviceCloseListenerStreamController.add(call.arguments);
          break;
        }
      default:
        print("没有找到方法:${method}");
    }
    return new Future.value("");
  }

  //初始化
  static init() async {
    _channel.setMethodCallHandler(handler); //注意这里需要设置一下监听函数
    return await _channel.invokeMethod('init');
  }

  ////蓝牙打开状态
  static Future<bool> isBluetoothOpen() async {
    return await _channel.invokeMethod('isBluetoothOpen');
  }

  //打开蓝牙
  static openBluetooth() async {
    return await _channel.invokeMethod('openBluetooth');
  }

  //扫描蓝牙
  static startLeScan() {
    return _channel.invokeMethod('startLeScan');
  }

  //停止扫描
  static Future<void> stopLeScan() async {
    return await _channel.invokeMethod('stopLeScan');
  }

  //清空扫描缓存
  static Future<void> cleanScanCache() async {
    return await _channel.invokeMethod('cleanScanCache');
  }

  //连接
  static Future<void> connect(String macAddress) async {
    return await _channel.invokeMethod('connect', macAddress);
  }

  //断开
  static Future<void> disconnect() async {
    return await _channel.invokeMethod('disconnect');
  }

  //是否扫描
  static Future<bool> isConnect() async {
    return await _channel.invokeMethod('isConnect');
  }

  //停止扫描并断开连接
  static Future<void> stopScanAndDisconnect() async {
    return await _channel.invokeMethod('stopScanAndDisconnect');
  }

  //设置管理员
  static Future<bool> setAdmin(String id, String dateTime) async {
    return await _channel
        .invokeMethod('setAdmin', {"id": id, "dateTime": dateTime});
  }

  //设置时间
  static Future<void> setTime(String id, String dateTime) async {
    return await _channel
        .invokeMethod('setTime', {"id": id, "dateTime": dateTime});
  }

  //开门
  static Future<void> openDoor(
      String id,
      String nowDateTime,
      String limitBeginTime,
      String limitEndTime,
      String startTimeDay1,
      String endTimeDay1,
      String startTimeDay2,
      String endTimeDay2) async {
    return await _channel.invokeMethod('openDoor', {
      "id": id,
      "nowDateTime": nowDateTime,
      "limitBeginTime": limitBeginTime,
      "limitEndTime": limitEndTime,
      "startTimeDay1": startTimeDay1,
      "endTimeDay1": endTimeDay1,
      "startTimeDay2": startTimeDay2,
      "endTimeDay2": endTimeDay2
    });
  }

  //修改密码
  static Future<void> modifyPassowrd(String id, String password) async {
    return await _channel
        .invokeMethod('modifyPassowrd', {"id": id, "password": password});
  }

  //查询锁信息
  static Future<void> queryLockDetails(String id) async {
    return await _channel.invokeMethod('queryLockDetails', id);
  }
}
