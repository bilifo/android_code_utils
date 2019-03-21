package com.example.a1.usbtest;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.chrisplus.rootmanager.RootManager;
import com.chrisplus.rootmanager.container.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    EditText ed;
    Button bt;
    UsbManager usbManager;

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        quanxian6();
        tv = findViewById(R.id.textView);
        bt = findViewById(R.id.button);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Result b = RootManager.getInstance().runCommand("chmod 666 /data/data/");
                String shpath = "/data/data/" + getPackageName() + "/test.sh";
                try {
                    copyFromAssets(getAssets(), "test.sh", shpath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                StringBuffer command = new StringBuffer();
                command.append("sh ");
                command.append(shpath);

                Result c = RootManager.getInstance().runCommand(command.toString());
                tv.setText(c.getMessage());
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.device);
        }
    }

    private void usbPermission(UsbDevice usbDevice) {
        if (!usbManager.hasPermission(usbDevice)) {
            Log.d("pjl++", "usbPermission:usbDevice:  " + usbDevice.getDeviceName());
            PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, (int) (Math.random() * 100), new Intent(ACTION_DEVICE_PERMISSION), 0);
            usbManager.requestPermission(usbDevice, pi);
        } else {
            Log.d("pjl++", "" + usbDevice.getDeviceName() + " is Permission");
        }
        int i = usbDevice.getInterfaceCount();
        for (int j = 0; j < i; j++) {
            UsbInterface in = usbDevice.getInterface(j);
//            Log.d("pjl++", "UsbInterface:  " + in.getName());
            int i2 = in.getEndpointCount();
            for (int j2 = 0; j2 < i2; j2++) {
                UsbEndpoint ue = in.getEndpoint(j2);
                Log.d("pjl++", "UsbEndpoint:  " + ue);
            }
        }

    }

    private String ACTION_DEVICE_PERMISSION = "1";
    private BroadcastReceiver mUsbPermissionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_DEVICE_PERMISSION.equals(action)) {
                synchronized (this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        //授权成功,在这里进行打开设备操作
                        //获得了usb使用权限
                        Log.d("pjl++", "onReceive: 获得了usb使用权限,授权成功");
                    } else {
                        //授权失败
                    }
                }
            }
        }

    };

//Java原生的调指令并获得结果方法
//    public StringBuffer runRootCommand(String command) {
//        Process process = null;
//        StringBuffer output = null;
//        try {
//            process = Runtime.getRuntime().exec(command);
//            output = new StringBuffer();
////            DataInputStream dis=new DataInputStream(process.getInputStream());
//            String line;
//
//            BufferedReader dis = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            while ((line = dis.readLine()) != null) {
//                output.append(line).append('\n');
//            }
//            process.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return output;
//    }

    public static void copyFromAssets(AssetManager assets, String source, String dest)
            throws IOException {
        File file = new File(dest);
        if (!file.exists()) {
            file.createNewFile();
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = assets.open(source);
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int size = 0;
            while ((size = is.read(buffer, 0, 1024)) >= 0) {
                fos.write(buffer, 0, size);
            }
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
            }
        }
    }


    /**
     * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
     */
    public void quanxian6() {
        if (Build.VERSION.SDK_INT >= 23)

        {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }

}
