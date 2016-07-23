/*
 * Copyright Cypress Semiconductor Corporation, 2014-2015 All rights reserved.
 * 
 * This software, associated documentation and materials ("Software") is
 * owned by Cypress Semiconductor Corporation ("Cypress") and is
 * protected by and subject to worldwide patent protection (UnitedStates and foreign), United States copyright laws and international
 * treaty provisions. Therefore, unless otherwise specified in a separate license agreement between you and Cypress, this Software
 * must be treated like any other copyrighted material. Reproduction,
 * modification, translation, compilation, or representation of this
 * Software in any other form (e.g., paper, magnetic, optical, silicon)
 * is prohibited without Cypress's express written permission.
 * 
 * Disclaimer: THIS SOFTWARE IS PROVIDED AS-IS, WITH NO WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING, BUT NOT LIMITED TO,
 * NONINFRINGEMENT, IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE. Cypress reserves the right to make changes
 * to the Software without notice. Cypress does not assume any liability
 * arising out of the application or use of Software or any product or
 * circuit described in the Software. Cypress does not authorize its
 * products for use as critical components in any products where a
 * malfunction or failure may reasonably be expected to result in
 * significant injury or death ("High Risk Product"). By including
 * Cypress's product in a High Risk Product, the manufacturer of such
 * system or application assumes all risk of such use and in doing so
 * indemnifies Cypress against all liability.
 * 
 * Use of this Software may be limited by and subject to the applicable
 * Cypress software license agreement.
 * 
 * 
 */

package com.patr.radix.utils;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.patr.radix.R;
import com.patr.radix.ble.BluetoothLeService;

/**
 * Class for commonly used methods in the project
 */
public class Utils {

    // Shared preference constant
    private static String SHARED_PREF_NAME = "CySmart Shared Preference";

    /**
     * Returns the manufacture name from the given characteristic
     *
     * @param characteristic
     * @return manfacture_name_string
     */
    @SuppressLint("NewApi")
    public static String getManufacturerNameString(
            BluetoothGattCharacteristic characteristic) {
        String manfacture_name_string = characteristic.getStringValue(0);
        return manfacture_name_string;
    }

    /**
     * Returns the model number from the given characteristic
     *
     * @param characteristic
     * @return model_name_string
     */

    @SuppressLint("NewApi")
    public static String getModelNumberString(
            BluetoothGattCharacteristic characteristic) {
        String model_name_string = characteristic.getStringValue(0);

        return model_name_string;
    }

    /**
     * Returns the serial number from the given characteristic
     *
     * @param characteristic
     * @return serial_number_string
     */
    @SuppressLint("NewApi")
    public static String getSerialNumberString(
            BluetoothGattCharacteristic characteristic) {
        String serial_number_string = characteristic.getStringValue(0);

        return serial_number_string;
    }

    /**
     * Returns the hardware number from the given characteristic
     *
     * @param characteristic
     * @return hardware_revision_name_string
     */
    @SuppressLint("NewApi")
    public static String getHardwareRevisionString(
            BluetoothGattCharacteristic characteristic) {
        String hardware_revision_name_string = characteristic.getStringValue(0);

        return hardware_revision_name_string;
    }
    /**
     * Returns the Firmware number from the given characteristic
     *
     * @param characteristic
     * @return hardware_revision_name_string
     */
    @SuppressLint("NewApi")
    public static String getFirmwareRevisionString(
            BluetoothGattCharacteristic characteristic) {
        String firmware_revision_name_string = characteristic.getStringValue(0);

        return firmware_revision_name_string;
    }
    /**
     * Returns the software revision number from the given characteristic
     *
     * @param characteristic
     * @return hardware_revision_name_string
     */
    @SuppressLint("NewApi")
    public static String getSoftwareRevisionString(
            BluetoothGattCharacteristic characteristic) {
        String hardware_revision_name_string = characteristic.getStringValue(0);

        return hardware_revision_name_string;
    }

    /**
     * Returns the PNP ID from the given characteristic
     *
     * @param characteristic
     * @return {@link String}
     */
    @SuppressLint("NewApi")
    public static String getPNPID(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        if (data != null && data.length > 0) {
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
        }

        return String.valueOf(stringBuilder);
    }

    /**
     * Returns the SystemID from the given characteristic
     *
     * @param characteristic
     * @return {@link String}
     */
    @SuppressLint("NewApi")
    public static String getSYSID(BluetoothGattCharacteristic characteristic) {
        final byte[] data = characteristic.getValue();
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        if (data != null && data.length > 0) {
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
        }

        return String.valueOf(stringBuilder);
    }

    /**
     * Adding the necessary INtent filters for Broadcast receivers
     *
     * @return {@link IntentFilter}
     */
    public static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED_CAROUSEL);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_ERROR);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CHARACTERISTIC_WRITE_SUCCESS);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DESCRIPTORWRITE_RESULT);
        return intentFilter;
    }

    public static String ByteArraytoHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();

    }

    public static byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            StringBuilder stringBuilder = new StringBuilder(s);
            stringBuilder.insert(s.length()-1,"0");
            s = stringBuilder.toString();
        }


        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static boolean isRightHexStr(String str){
        String reg = "^[0-9a-fA-F]+$";
        return str.matches(reg);
    }



    public static String getMSB(String string) {
        StringBuilder msbString = new StringBuilder();

        for (int i = string.length(); i > 0; i -= 2) {
            String str = string.substring(i - 2, i);
            msbString.append(str);
        }
        return msbString.toString();
    }

    /**
     * Converting the Byte to binary
     *
     * @param bytes
     * @return {@link String}
     */
    public static String BytetoBinary(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
        for (int i = 0; i < Byte.SIZE * bytes.length; i++)
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
                    : '1');
        return sb.toString();
    }

    public static String byteToASCII(byte[] array) {

        StringBuffer sb = new StringBuffer();
        for (byte byteChar : array) {
            if (byteChar >= 32 && byteChar < 127) {
                sb.append(String.format("%c", byteChar));
            } else {
                sb.append(String.format("%d ", byteChar & 0xFF)); // to convert
                // >127 to
                // positive
                // value
            }
        }
        return sb.toString();
    }

    /**
     * Returns the battery level information from the characteristics
     *
     * @param characteristics
     * @return {@link String}
     */
    @SuppressLint("NewApi")
    public static String getBatteryLevel(
            BluetoothGattCharacteristic characteristics) {
        int battery_level = characteristics.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return String.valueOf(battery_level);
    }

    /**
     * Returns the Alert level information from the characteristics
     *
     * @param characteristics
     * @return {@link String}
     */
    @SuppressLint("NewApi")
    public static String getAlertLevel(
            BluetoothGattCharacteristic characteristics) {
        int alert_level = characteristics.getIntValue(
                BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        return String.valueOf(alert_level);
    }

    /**
     * Returns the Transmission power information from the characteristic
     *
     * @param characteristics
     * @return {@link integer}
     */
    @SuppressLint("NewApi")
    public static int getTransmissionPower(
            BluetoothGattCharacteristic characteristics) {
        int power_level = characteristics.getIntValue(
                BluetoothGattCharacteristic.FORMAT_SINT8, 0);
        return power_level;
    }

    /**
     * Returns the Date from the long milliseconds
     *
     * @param date in millis
     * @return {@link String}
     */
    public static String GetDateFromLong(long date) {
        Date currentDate = new Date(date);
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        //formatted value of current Date
        // System.out.println("Milliseconds to Date: " + formatter.format(currentDate));
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        //System.out.println("Milliseconds to Date using Calendar:"
        //        + formatter.format(cal.getTime()));
        return currentDate.toString();

    }

    /**
     * Get the data from milliseconds
     *
     * @return {@link String}
     */
    public static String GetDateFromMilliseconds() {
        DateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    /**
     * Get the date
     *
     * @return {@link String}
     */
    public static String GetDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }
    /**
     * Get the time in seconds
     *
     * @return {@link String}
     */
    public static int getTimeInSeconds() {
        int seconds = (int) System.currentTimeMillis();
        return seconds;
    }
    /**
     * Get the seven days before date
     *
     * @return {@link String}
     */

    public static String GetDateSevenDaysBack() {
        DateFormat formatter = new SimpleDateFormat("dd_MMM_yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -7);
        return formatter.format(calendar.getTime());

    }

    /**
     * Get the time from milliseconds
     *
     * @return {@link String}
     */
    public static String GetTimeFromMilliseconds() {
        DateFormat formatter = new SimpleDateFormat("HH:mm ss SSS");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }

    /**
     * Get time and date
     *
     * @return {@link String}
     */

    public static String GetTimeandDate() {
        DateFormat formatter = new SimpleDateFormat("[dd-MMM-yyyy|HH:mm:ss]");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }
    /**
     * Get time and date without datalogger format
     *
     * @return {@link String}
     */

    public static String GetTimeandDateUpdate() {
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());

    }
    /**
     * Setting the shared preference with values provided as parameters
     *
     * @param context
     * @param key
     * @param value
     */
    public static final void setStringSharedPreference(Context context,
                                                       String key, String value) {
        SharedPreferences goaPref = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = goaPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Returning the stored values in the shared preference with values provided
     * as parameters
     *
     * @param context
     * @param key
     * @return
     */
    public static final String getStringSharedPreference(Context context,
                                                         String key) {
        if (context != null) {

            SharedPreferences Pref = context.getSharedPreferences(
                    SHARED_PREF_NAME, Context.MODE_PRIVATE);
            String value = Pref.getString(key, "");
            return value;

        } else {
            return "";
        }
    }

    /**
     * Setting the shared preference with values provided as parameters
     *
     * @param context
     * @param key
     * @param value
     */
    public static final void setIntSharedPreference(Context context,
                                                    String key, int value) {
        SharedPreferences goaPref = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = goaPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Returning the stored values in the shared preference with values provided
     * as parameters
     *
     * @param context
     * @param key
     * @return
     */
    public static final int getIntSharedPreference(Context context,
                                                   String key) {
        if (context != null) {

            SharedPreferences Pref = context.getSharedPreferences(
                    SHARED_PREF_NAME, Context.MODE_PRIVATE);
            int value = Pref.getInt(key, 0);
            return value;

        } else {
            return 0;
        }
    }

    /**
     * Take the screen shot of the device
     *
     * @param view
     */
    public static void screenShotMethod(View view) {
        Bitmap bitmap;
        if (view != null) {
            View v1 = view;
            v1.setDrawingCacheEnabled(true);
            v1.buildDrawingCache(true);
            bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "CySmart" + File.separator + "file.jpg");
            try {
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method to detect whether the device is phone or tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    /**
     * Check whether Internet connection is enabled on the device
     *
     * @param context
     * @return
     */
    public static final boolean checkNetwork(Context context) {
        if (context != null) {
            boolean result = true;
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnectedOrConnecting()) {
                result = false;
            }
            return result;
        } else {
            return false;
        }
    }

    public void toast(Activity context, String text) {
        Toast.makeText(context, text.toString(), Toast.LENGTH_LONG).show();
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getPorperties(Context context,BluetoothGattCharacteristic item){
        String proprties;
        String read = null, write = null, notify = null;

        /**
         * Checking the various GattCharacteristics and listing in the ListView
         */
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_READ)) {
            read = context.getString(R.string.gatt_services_read);
        }
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE)
                | getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) {
            write = context.getString(R.string.gatt_services_write);
        }
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_NOTIFY)) {
            notify = context.getString(R.string.gatt_services_notify);
        }
        if (getGattCharacteristicsPropertices(item.getProperties(),
                BluetoothGattCharacteristic.PROPERTY_INDICATE)) {
            notify = context.getString(R.string.gatt_services_indicate);
        }
        // Handling multiple properties listing in the ListView
        if (read != null) {
            proprties = read;
            if (write != null) {
                proprties = proprties + " & " + write;
            }
            if (notify != null) {
                proprties = proprties + " & " + notify;
            }
        } else {
            if (write != null) {
                proprties = write;

                if (notify != null) {
                    proprties = proprties + " & " + notify;
                }
            } else {
                proprties = notify;
            }
        }

        return proprties;
    }


    // Return the properties of mGattCharacteristics
    public static boolean getGattCharacteristicsPropertices(int characteristics,int characteristicsSearch) {

        if ((characteristics & characteristicsSearch) == characteristicsSearch) {
            return true;
        }
        return false;
    }

    public static int dpToPx(int dp){
        return  (int)(dp* Resources.getSystem().getDisplayMetrics().density);
    }
    
    public static String getImsi(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        return imsi;
    }
    
    public static String getCsn(Context context) {
        String imsi = getImsi(context);
        String csn = Constants.DEFAULT_CSN;
        if (imsi != null && imsi.length() >= 8) {
            csn = imsi.substring(imsi.length() - 8);
        }
        return csn;
    }

    public static final InputStream byte2Input(byte[] buf) {
        return new ByteArrayInputStream(buf);
    }

    public static final byte[] input2byte(InputStream inStream)
            throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 判断是否闰年
     * 
     * @param year
     * @return
     */
    public static boolean isLeap(int year) {
        return (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
    }

    /**
     * 时间算法
     *     Byte3       Byte2       Byte1     Byte0
     *  [yyyyyydd] [dddmmmms] [ssssssss][ssssssss]
     * @param time
     * @return
     */
    public static byte[] dateTime2Bytes(Date time) {
        byte[] data = new byte[4];
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time.getTime());
        
        int y = cal.get(Calendar.YEAR)-2000;
        int d = cal.get(Calendar.DATE);
        int m = cal.get(Calendar.MONTH)+1;
        int s = cal.get(Calendar.HOUR_OF_DAY)*3600 + cal.get(Calendar.MINUTE)*60 + cal.get(Calendar.SECOND);

        int i = 0;
        i = (y << 26);  //32-6=26 左移26位表示 日的长度+月的长度+秒的长度
        i |= (d << 21);
        i |= (m << 17);
        i |= s;

        data[3] = (byte)(i & 0xff);
        data[2] = (byte)((i >> 8) & 0xff);
        data[1] = (byte)((i >> 16) & 0xff);
        data[0] = (byte)((i >> 24) & 0xff);
        
        return data;
    }
    
    public static String getCmdData(String cmd, String data) {
        String dataText = data.replace(" ", "");
        String cmdText = cmd.replace(" ", "");
        byte[] dataArray = Utils.hexStringToByteArray(dataText);
        byte[] cmdArray = Utils.hexStringToByteArray(cmdText);
        byte[] check = { cmdArray[0] };
        for (byte b : dataArray) {
            check[0] ^= b;
        }
        return "AA " + cmd + data + Utils.ByteArraytoHex(check) + "DD";
    }
    
    public static byte[] getEncryptedCmdDate(String cmdData) {
        String text = cmdData.replace(" ", "");
        byte[] array = Utils.hexStringToByteArray(text);
        int size = array.length;
        for (int i = 0; i < size; i++) {
            array[i] ^= Constants.ENCRYPT;
        }
        return array;
    }

}
