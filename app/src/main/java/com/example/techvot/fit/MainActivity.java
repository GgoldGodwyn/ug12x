package com.example.techvot.fit;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity {


    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "FIT Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "FIT Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No FIT connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "FIT disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private TextView details;
    private EditText BVN;
    private Button comfirm;

    private Button getPrint;
    private UsbService usbService;

    R305 fitModule = new R305();


    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mHandler = new MyHandler(this);


    }

    public  void onClickSignPay(View v){
        setContentView(R.layout.activity_payment);
    }



    public void captureFinger(View v) {
        verifyPassword();
        GenImg();
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        //String hex = String.format("%04x", (int) ch);

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    //mActivity.get().details.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE",Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }




    ////////////fingerprint
    public void verifyPassword() {
        if (usbService != null) {

            int[] packet = {R305.FINGERPRINT_VERIFYPASSWORD, R305.thePassword, 0, 0, 0};

            writePacket(R305.theAddress, R305.FINGERPRINT_COMMANDPACKET, 7, packet);// FINGERPRINT_COMMANDPACKET is indentifier

            int idx = 0;
            int[] freply = new int[100];

        }
    }


    public byte GenImg() {
        if (usbService != null)
        {
            int[] packet = {R305.FINGERPRINT_GETIMAGE};
            writePacket(R305.theAddress, R305.FINGERPRINT_COMMANDPACKET, 3, packet);//
          /*  int idx = 0;
            int[] freply = new int[100];

            while (usbService.read(mCallback) < 1) ;
            while (usbService.read(mCallback) > 0) {
                //serialPort1.ReadTimeout != 0 ||
                freply[idx] = usbService.read(mCallback);
                idx++;
            }
            return 0;*/
        }

        return 0 ;
    }


    public void writePacket(int Addr, int PacketCMD,int LEN, int[]packet){

        byte [] sc={((byte)((R305.FINGERPRINT_STARTCODE>>8)&0xff)),(byte)(R305.FINGERPRINT_STARTCODE & 0xff)};
        byte [] address={(byte)((Addr>>24) &0xff),(byte)((Addr>>16) &0xff),(byte)((Addr>>8) &0xff),(byte)(Addr&0xff)};//
        byte [] packetType ={(byte)(PacketCMD & 0xFF) };//
        byte [] len ={(byte)((LEN>>8) &0xff),(byte)(LEN &0xff)};//
        usbService.write(sc);
        usbService.write(address);
        usbService.write(packetType);
        usbService.write(len);
        int sum = PacketCMD;
        sum += LEN;
        for(int i= 0; i<LEN-2; i++){
            //dat8 = (int)packet[i];
            byte []sca = {(byte)packet[i]};    //sc = BitConverter.GetBytes(dat8);
            usbService.write(sca);             //serialPort1.write(sc,0,1);
            sum += (int)packet[i];             //sum += (ushort)packet[i];
        }

        byte []arr = {(byte)((sum>> 8) & 0xff),(byte)(sum & 0xff)};
        usbService.write(arr);
    }

}








