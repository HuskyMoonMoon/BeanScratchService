package th.ac.kmitl.beanscratchservice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chayapol on 17-Aug-16.
 */
public class BeanScratchService
{
    // Static members and constants
    public static final String ScratchServiceID = "a495ff20-c5b1-4b44-b512-1370f02d74de";
    public static final String Scratch1CharacteristicID = "a495ff21-c5b1-4b44-b512-1370f02d74de";
    public static final String Scratch2CharacteristicID = "a495ff22-c5b1-4b44-b512-1370f02d74de";
    public static final String Scratch3CharacteristicID = "a495ff23-c5b1-4b44-b512-1370f02d74de";
    public static final String Scratch4CharacteristicID = "a495ff24-c5b1-4b44-b512-1370f02d74de";
    public static final String Scratch5CharacteristicID = "a495ff25-c5b1-4b44-b512-1370f02d74de";

    public static int[] convertToIntArray(byte[] byteData)
    {
        int[] accelValue = new int[3];
        for (int i = 0; i < byteData.length; i += 2)
        {
            byte[] byteValue = {byteData[i], byteData[i + 1]};
            accelValue[i / 2] = (short) BitConverter.toInt16(byteValue, 0);
        }
        return accelValue;
    }

    //Object member
    private Context Context;
    private BluetoothAdapter BluetoothAdapter;
    private boolean IsScanning;
    private Handler Handler;
    private LeScanCallback LeScanCallback;
    private OnBluetoothDeviceListChangedListener OnBluetoothDeviceListChangedListener;
    private ArrayList<BluetoothDevice> BluetoothDeviceList = new ArrayList<BluetoothDevice>()
    {
        @Override
        public boolean add(BluetoothDevice bluetoothDevice)
        {
            boolean isAdded = super.add(bluetoothDevice);

            if(OnBluetoothDeviceListChangedListener != null)
                OnBluetoothDeviceListChangedListener.onBluetoothDeviceListChanged();
            return isAdded;
        }

        @Override
        public BluetoothDevice remove(int index)
        {
            BluetoothDevice device = super.remove(index);
            if(OnBluetoothDeviceListChangedListener != null)
                OnBluetoothDeviceListChangedListener.onBluetoothDeviceListChanged();
            return device;
        }

        @Override
        public boolean contains(Object o)
        {
            BluetoothDevice device = (BluetoothDevice) o;
            for (BluetoothDevice d : this)
            {
                if (d.getName() != null && d.getAddress().equals(device.getAddress()) && d.getName().equals(device.getName()))
                    return true;
            }
            return false;
        }
    };

    private LeScanCallback InternalLeScanCallback = new LeScanCallback()
    {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            if (!BluetoothDeviceList.contains(device))
            {
                BluetoothDeviceList.add(device);
            }

            if (LeScanCallback != null)
                LeScanCallback.onLeScan(device, rssi, scanRecord);
        }
    };

    public BeanScratchService(Context context, BluetoothAdapter bluetoothAdapter)
    {
        setContext(context);
        setBluetoothAdapter(bluetoothAdapter);
    }

    public void startLEScan(boolean scanEnabled)
    {
        int ScanTimeout = 10000;
        if (scanEnabled)
        {
            getHandler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    IsScanning = false;
                    getBluetoothAdapter().stopLeScan(InternalLeScanCallback);
                }
            }, ScanTimeout);

            IsScanning = true;
            getBluetoothAdapter().startLeScan(InternalLeScanCallback);
        } else
        {
            IsScanning = false;
            getBluetoothAdapter().stopLeScan(InternalLeScanCallback);
        }
    }

    public BeanDevice connectAsBeanDevice(BluetoothDevice device, BluetoothGattCallback gattCallback) throws Exception
    {
        BeanDevice beanDevice = new BeanDevice(device, getContext());
        beanDevice.setGattCallback(gattCallback);
        if (!beanDevice.connect())
            throw new Exception("Can't connect to the specific device");
        return beanDevice;
    }

    public android.bluetooth.BluetoothAdapter getBluetoothAdapter()
    {
        return BluetoothAdapter;
    }

    public void setBluetoothAdapter(android.bluetooth.BluetoothAdapter bluetoothAdapter)
    {
        BluetoothAdapter = bluetoothAdapter;
    }

    public android.os.Handler getHandler()
    {
        return Handler;
    }

    public void setHandler(android.os.Handler handler)
    {
        Handler = handler;
    }

    public Context getContext()
    {
        return Context;
    }

    public void setContext(Context context)
    {
        Context = context;
    }

    public LeScanCallback getLeScanCallback()
    {
        return this.LeScanCallback;
    }

    public void setLeScanCallback(LeScanCallback leScanCallback)
    {
        this.LeScanCallback = leScanCallback;
    }

    public boolean isScanning()
    {
        return IsScanning;
    }

    public List<BluetoothDevice> getBluetoothDeviceList()
    {
        return BluetoothDeviceList;
    }

    public void setOnBluetoothDeviceListChangedListener(BeanScratchService.OnBluetoothDeviceListChangedListener onBluetoothDeviceListChangedListener)
    {
        OnBluetoothDeviceListChangedListener = onBluetoothDeviceListChangedListener;
    }

    public interface OnBluetoothDeviceListChangedListener
    {
        abstract void onBluetoothDeviceListChanged();
    }

}
