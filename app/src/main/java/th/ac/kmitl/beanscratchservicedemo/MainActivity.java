package th.ac.kmitl.beanscratchservicedemo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import th.ac.kmitl.beanscratchservice.BeanDevice;
import th.ac.kmitl.beanscratchservice.BeanScratchService;
import th.ac.kmitl.beanscratchservice.OnCharacteristicChangedCallback;

public class MainActivity extends AppCompatActivity
{
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    BeanDevice beanDevice;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_COARSE_LOCATION:
            {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("Bean", "coarse location permission granted");
                    startBtConnect();
                } else
                {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                    {

                        @Override
                        public void onDismiss(DialogInterface dialog)
                        {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialog)
                    {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
            else
                startBtConnect();
        }
        else
            startBtConnect();
    }

    void startBtConnect()
    {
        BluetoothManager btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        Utility.BeanScratchService = new BeanScratchService(getApplicationContext(), btManager.getAdapter());
        Handler handler = new Handler(Looper.getMainLooper());
        Utility.BeanScratchService.setHandler(handler);
        Utility.BeanScratchService.setLeScanCallback(new BluetoothAdapter.LeScanCallback()
        {
            @Override
            public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes)
            {
                Log.i("Bean", "Finding");
                if (bluetoothDevice.getName() != null &&
                        (bluetoothDevice.getName().equalsIgnoreCase("beanbait1") ||
                                bluetoothDevice.getName().equalsIgnoreCase("bean")))
                {
                    try
                    {
                        beanDevice = Utility.BeanScratchService.connectAsBeanDevice(bluetoothDevice, null);
                        beanDevice.setCharacteristicChangedCallback(new OnCharacteristicChangedCallback()
                        {
                            @Override
                            public void onCharacteristic1Changed(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
                            {
                                appendToLog(characteristic);
                            }

                            @Override
                            public void onCharacteristic2Changed(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
                            {
                                appendToLog(characteristic);
                            }

                            void appendToLog(BluetoothGattCharacteristic characteristic)
                            {
                                int[] accelData = BeanScratchService.convertToIntArray(characteristic.getValue());
                                Log.i("Bean", String.format("x:%d, y%d, z%d", accelData[0], accelData[1], accelData[2]));
                            }
                        });
                        beanDevice.connect();
                    } catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
        Utility.BeanScratchService.startLEScan(true);
    }

}
