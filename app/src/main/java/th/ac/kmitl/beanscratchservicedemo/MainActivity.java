package th.ac.kmitl.beanscratchservicedemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import th.ac.kmitl.beanscratchservice.BeanDevice;
import th.ac.kmitl.beanscratchservice.BeanScratchService;
import th.ac.kmitl.beanscratchservice.OnCharacteristicChangedCallback;
public class MainActivity extends AppCompatActivity
{
    BeanDevice beanDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                if(bluetoothDevice.getName() != null &&
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
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        });
        Utility.BeanScratchService.startLEScan(true);
    }
}
