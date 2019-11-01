package cn.bohaisoft.open_lock;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.smartlockbluetoothlib.bean.Card;
import com.smartlockbluetoothlib.bean.Fingerprint;
import com.smartlockbluetoothlib.bean.UnlockRecord;
import com.smartlockbluetoothlib.service.BluetoothResponsesListener;
import com.smartlockbluetoothlib.service.BluetoothService;
import com.smartlockbluetoothlib.service.OperationLock;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * OpenLockPlugin
 */
public class OpenLockPlugin implements MethodCallHandler {
    private final Activity activity;
    private MethodChannel channel;
    private BluetoothService bluetoothService;
    private final static String TAG = "openLockPlugin";
    SimpleDateFormat simpleDateFormat;

    /**
     * 插件注册.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "open_lock");
        channel.setMethodCallHandler(new OpenLockPlugin(registrar, channel));
    }

    /**
     * 构造函数
     */
    public OpenLockPlugin(Registrar registrar, MethodChannel channel) {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        this.activity = registrar.activity();
        this.channel = channel;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        initPermission();
        switch (call.method) {
            case "init":
                init(call, result);
                break;
            case "isBluetoothOpen":
                isBluetoothOpen(call, result);
                break;
            case "openBluetooth":
                openBluetooth(call, result);
                break;
            case "startLeScan":
                startLeScan(call, result);
                break;
            case "stopLeScan":
                stopLeScan(call, result);
                break;
            case "cleanScanCache":
                cleanScanCache(call, result);
                break;
            case "connect":
                connect(call, result);
                break;
            case "disconnect":
                disconnect(call, result);
                break;
            case "isConnect":
                isConnect(call, result);
                break;
            case "stopScanAndDisconnect":
                stopScanAndDisconnect(call, result);
                break;
            case "setAdmin":
                setAdmin(call, result);
                break;
            case "setTime":
                setTime(call, result);
                break;
            case "openDoor":
                openDoor(call, result);
                break;
            case "modifyPassowrd":
                modifyPassowrd(call, result);
                break;
            case "queryLockDetails":
                queryLockDetails(call, result);
                break;
            default:
                Log.e(TAG, "没有找到这个方法:" + call.method);
                break;
        }
    }

    /**
     * android 6.0 以上需要动态申请权限
     */
    private void initPermission() {
        //语音识别需要的权限
        String permissions[] = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.BLUETOOTH_PRIVILEGED,
                Manifest.permission.READ_PHONE_STATE
        };

        // 需申请的权限列表
        ArrayList<String> toApplyList = new ArrayList<String>();

        // 检查权限
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(activity, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            //请求权限
            ActivityCompat.requestPermissions(activity, toApplyList.toArray(tmpList), 123);
        }

    }

    //蓝牙初始化
    private void init(MethodCall call, Result result) {
        try {
            bluetoothService = BluetoothService.getInstance(this.activity);
            bluetoothService.setBtListener(bluetoothResponsesListener);
            if (bluetoothService != null) {
                Log.e(TAG, "蓝牙初始化成功");
                result.success(true);
            } else {
                Log.e(TAG, "蓝牙初始化失败");
                result.success(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "蓝牙初始化失败");
        }
    }

    //蓝牙打开状态
    private void isBluetoothOpen(MethodCall call, Result result) {
        if (bluetoothService != null) {
            boolean isOpen = bluetoothService.isBluetoothOpen();
            Log.e(TAG, "蓝牙状态:" + isOpen);
            result.success(isOpen);
        } else {
            Log.e(TAG, "未初始化");
        }
    }

    //打开蓝牙
    private void openBluetooth(MethodCall call, Result result) {
        bluetoothService.openBluetooth(activity, 0);
    }

    //扫描蓝牙
    private void startLeScan(MethodCall call, Result result) {
        bluetoothService.startLeScan(bluetoothResponsesListener);
    }

    //停止扫描
    private void stopLeScan(MethodCall call, Result result) {
        try {
            bluetoothService.stopLeScan();
            result.success(true);
        } catch (Exception e) {
            result.error("error", e.getMessage(), false);
        }
    }

    //清空扫描缓存
    private void cleanScanCache(MethodCall call, Result result) {
        bluetoothService.cleanScanCache();
    }

    //连接
    private void connect(MethodCall call, Result result) {
        bluetoothService.connect((String) call.arguments);
    }

    //断开
    private void disconnect(MethodCall call, Result result) {
        bluetoothService.disconnect();
    }

    //是否扫描
    private void isConnect(MethodCall call, Result result) {
        result.success(bluetoothService.isConnect());
    }

    //停止扫描并断开连接
    private void stopScanAndDisconnect(MethodCall call, Result result) {
        bluetoothService.stopScanAndDisconnect();
    }

    //设置管理员
    private void setAdmin(MethodCall call, Result result) {
        try {
            Date date = simpleDateFormat.parse((String) call.argument("dateTime"));
            result.success(bluetoothService.setAdmin((String) call.argument("id"), date));
        } catch (Exception e) {
            Log.e(TAG, "日期时间格式不正确!");
        }
    }

    //设置时间
    private void setTime(MethodCall call, Result result) {
        try {
            Date date = simpleDateFormat.parse((String) call.argument("dateTime"));
            bluetoothService.setTime((String) call.argument("id"), date);
        } catch (Exception e) {
            Log.e(TAG, "日期时间格式不正确!");
        }

    }

    //开门
    private void openDoor(MethodCall call, Result result) {
        try {
            Log.e(TAG, call.arguments.toString());
            Date nowDateTime = simpleDateFormat.parse((String) call.argument("nowDateTime"));
            Date limitBeginTime = simpleDateFormat.parse((String) call.argument("limitBeginTime"));
            Date limitEndTime = simpleDateFormat.parse((String) call.argument("limitEndTime"));
            //管理员id , OperationLock,现在时间,限时时间开始,限时时间结束,每天开始时间1,每天结束时间1,每天开始时间2,每天结束时间2
            bluetoothService.openDoor(call.argument("id").toString(),
                    OperationLock.Open,
                    nowDateTime,
                    limitBeginTime,
                    limitEndTime,
                    (String) call.argument("startTimeDay1"),
                    (String) call.argument("endTimeDay"),
                    (String) call.argument("startTimeDay2"),
                    (String) call.argument("endTimeDay2"));
        } catch (Exception e) {
            Log.e(TAG, "日期时间格式不正确+1:" + e.getMessage());
        }

    }

    //修改密码
    private void modifyPassowrd(MethodCall call, Result result) {
        bluetoothService.modifyPassowrd((String) call.argument("id"),
                (String) call.argument("password"));
    }

    //查询锁信息
    private void queryLockDetails(MethodCall call, Result result) {
        bluetoothService.queryLockDetails((String) call.arguments);
    }

    //回调
    private BluetoothResponsesListener bluetoothResponsesListener = new BluetoothResponsesListener() {

        //监听连接
        @Override
        public void connectListener(String s, String s1) {
            Log.e(TAG, "监听连接:" + s + "|" + s1);
        }

        //断开监听
        @Override
        public void disconnectListener(String s, String s1) {
            Log.e(TAG, "监听断开:" + s + "|" + s1);
        }

        //设备关闭
        @Override
        public void deviceClose(String s, String s1) {
            Log.e(TAG, "关闭设备:" + s + "|" + s1);
            channel.invokeMethod("deviceClose",  true);
        }

        //连接失败监听
        @Override
        public void connectFailListener(int i, String s) {
            Log.e(TAG, "连接失败监听:" + i + "|" + s);
//            channel.invokeMethod("connectFailListener",  s + "|");
        }

        //扫描监听
        @Override
        public void leScanListener(String s, String s1) {
            Log.e(TAG, "扫描到设备:" + s + "|" + s1);
            channel.invokeMethod("scanListener", s + "|" + s1);
        }

        //发送命令状态
        @Override
        public void onSendCmdState(int i, boolean b) {
            Log.e(TAG, "发送命令状态:" + i + "" + b);
        }

        //未知错误
        @Override
        public void onUnknownError() {

        }

        //重置设备
        @Override
        public void onResetDevice(int i) {

        }

        //设置管理员
        @Override
        public void onSettingAdmin(int i) {
            Log.e(TAG, "设置管理员:" + i);
        }

        //解锁
        @Override
        public void onUnlock(int i) {
            Log.e(TAG, "解锁:" + i);
        }

        //设置动态密码
        @Override
        public void onSettingDynPasswd(int i) {

        }

        //查询锁信息
        @Override
        public void queryLockDetails(int i, String s, Float aFloat, String s1, Integer integer) {
            Log.e(TAG, "输出:" + i + "|" + s + "|" + aFloat + "|" + s1 + "|" + integer);
        }

        //修改密码
        @Override
        public void onModifyPasswd(int i) {

        }

        //设置时间
        @Override
        public void onSettingTime(int i) {
            Log.e(TAG, "设置时间:" + i);
        }

        //设置
        @Override
        public void onSet(int i, boolean b) {
            Log.e(TAG, "设置:" + i + "|" + b);
        }

        //解锁记录
        @Override
        public void onUnlockRecord(int i, List<UnlockRecord> list) {

        }

        //删除记录
        @Override
        public void onDeleteRecord(int i) {

        }

        //设置指纹
        @Override
        public void onSetFingerprint(int i) {

        }

        //指纹进度
        @Override
        public void onProgressFingerprint(int i, boolean b, int i1) {

        }

        //设置指纹完成
        @Override
        public void onSetFingerprintFinish(int i, Fingerprint fingerprint) {

        }

        //查询指纹
        @Override
        public void onQueryFingerprint(int i, List<Fingerprint> list) {

        }

        //编辑指纹
        @Override
        public void onEditFingerprint(int i) {

        }

        //删除指纹
        @Override
        public void onDeleteFingerprint(int i) {

        }

        //设置卡
        @Override
        public void onSetCard(int i, String s, String s1) {

        }

        //删除卡
        @Override
        public void onDeleteCard(int i) {

        }

        //查询卡
        @Override
        public void onQueryCard(int i, List<Card> list) {

        }

        @Override
        public void onPassword_(int i) {

        }

        @Override
        public void onPasswordOnce_(int i) {

        }

        @Override
        public void onDeletePasswordOnce_(int i) {

        }
    };

}



