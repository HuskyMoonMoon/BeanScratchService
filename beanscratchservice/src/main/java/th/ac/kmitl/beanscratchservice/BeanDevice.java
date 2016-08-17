package th.ac.kmitl.beanscratchservice;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;

import java.util.UUID;

/**
 * Created by Chayapol on 17-Aug-16.
 */
public class BeanDevice
{
    private Context Context;
    private BluetoothGattCharacteristic Characteristic1;
    private BluetoothGattCharacteristic Characteristic2;
    private BluetoothGattCharacteristic Characteristic3;
    private BluetoothGattCharacteristic Characteristic4;
    private BluetoothGattCharacteristic Characteristic5;
    private OnCharacteristicChangedCallback CharacteristicChangedCallback;
    private BluetoothGatt BluetoothGatt;
    private BluetoothDevice BluetoothDevice;
    private BluetoothGattCallback GattCallback;

    private BluetoothGattCallback InternalGattCallback = new BluetoothGattCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS)
            {
                gatt.discoverServices();
            }

            if (GattCallback != null)
                GattCallback.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            super.onServicesDiscovered(gatt, status);
            for (BluetoothGattService sv : gatt.getServices())
            {
                if (sv.getUuid().equals(UUID.fromString(BeanScratchService.ScratchServiceID)))
                {
                    Characteristic1 = sv.getCharacteristic(UUID.fromString(BeanScratchService.Scratch1CharacteristicID));
                    Characteristic2 = sv.getCharacteristic(UUID.fromString(BeanScratchService.Scratch2CharacteristicID));
                    Characteristic3 = sv.getCharacteristic(UUID.fromString(BeanScratchService.Scratch3CharacteristicID));
                    Characteristic4 = sv.getCharacteristic(UUID.fromString(BeanScratchService.Scratch4CharacteristicID));
                    Characteristic5 = sv.getCharacteristic(UUID.fromString(BeanScratchService.Scratch5CharacteristicID));
                    break;
                }
            }
            gatt.setCharacteristicNotification(Characteristic1, true);
            gatt.setCharacteristicNotification(Characteristic2, true);
            gatt.setCharacteristicNotification(Characteristic3, true);
            gatt.setCharacteristicNotification(Characteristic4, true);
            gatt.setCharacteristicNotification(Characteristic5, true);

            for (BluetoothGattDescriptor d : Characteristic1.getDescriptors())
            {
                if (d != null)
                {
                    d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(d);
                }
            }

            if (GattCallback != null)
                GattCallback.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
        {
            super.onCharacteristicChanged(gatt, characteristic);

            if (GattCallback != null)
                GattCallback.onCharacteristicChanged(gatt, characteristic);

            if (CharacteristicChangedCallback != null)
            {
                if (characteristic.getUuid().equals(UUID.fromString(BeanScratchService.Scratch1CharacteristicID)))
                    CharacteristicChangedCallback.onCharacteristic1Changed(gatt, characteristic);
                else if (characteristic.getUuid().equals(UUID.fromString(BeanScratchService.Scratch2CharacteristicID)))
                    CharacteristicChangedCallback.onCharacteristic2Changed(gatt, characteristic);
                else if (characteristic.getUuid().equals(UUID.fromString(BeanScratchService.Scratch3CharacteristicID)))
                    CharacteristicChangedCallback.onCharacteristic3Changed(gatt, characteristic);
                else if (characteristic.getUuid().equals(UUID.fromString(BeanScratchService.Scratch4CharacteristicID)))
                    CharacteristicChangedCallback.onCharacteristic4Changed(gatt, characteristic);
                else if (characteristic.getUuid().equals(UUID.fromString(BeanScratchService.Scratch5CharacteristicID)))
                    CharacteristicChangedCallback.onCharacteristic5Changed(gatt, characteristic);
            }
        }
    };


    public BeanDevice(BluetoothDevice device, Context context)
    {
        setBluetoothDevice(device);
        Context = context;
    }

    public BeanDevice()
    {

    }

    public boolean connect()
    {
        try
        {
            BluetoothGatt = BluetoothDevice.connectGatt(Context, true, InternalGattCallback);
            return true;
        } catch (Exception ex)
        {
            return false;
        }
    }

    public boolean disconnect()
    {
        try
        {
            if (BluetoothGatt == null)
                return true;
            BluetoothGatt.disconnect();
            BluetoothGatt.close();
            BluetoothGatt = null;
        } catch (Exception ex)
        {
            return false;
        }
        return true;
    }

    public void setBluetoothDevice(android.bluetooth.BluetoothDevice bluetoothDevice)
    {
        BluetoothDevice = bluetoothDevice;
    }

    public void setGattCallback(BluetoothGattCallback gattCallback)
    {
        GattCallback = gattCallback;
    }

    public void setCharacteristicChangedCallback(OnCharacteristicChangedCallback characteristicChangedCallback)
    {
        CharacteristicChangedCallback = characteristicChangedCallback;
    }

}
