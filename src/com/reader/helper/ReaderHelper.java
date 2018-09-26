package com.reader.helper;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

import com.reader.base.MessageTran;
import com.reader.base.ReaderBase;
import com.user.box.utils.Utils;

public class ReaderHelper {
    private ReaderBase mReader;
    private static Context mContext;

    private static ReaderHelper mReaderHelper1, mReaderHelper2;

    private Listener mListener = null;

    public interface Listener {
        public void onLostConnect();
        public void onRawInfo(byte[] btAryReceiveData);//debug
        public void onEPCInfo(byte[] btAryReceiveData);//epc
        public void onWeightInfo(byte[] btAryReceiveData);//weight
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public ReaderHelper() {}

    public ReaderHelper(Listener listener) {
        mListener = listener;
    }

    /**
     * 设置Context。
     * 
     * @param context 设置Context
     * @throws Exception 当Context为空时会抛出错误
     */
    public static void setContext(Context context) throws Exception {
        mContext = context;
        mReaderHelper1 = new ReaderHelper();
        mReaderHelper2 = new ReaderHelper();
    }

    /**
     * 返回helper中全局的读写器帮助类。
     * 
     * @return 返回helper中全局的读写器帮助类
     * @throws Exception
     *             当helper中全局的读写器帮助类为空时会抛出错误
     */
    public static ReaderHelper getDefaultHelper() throws Exception {
        if (mReaderHelper1 == null || mContext == null)
            throw new NullPointerException("mReaderHelper Or mContext is Null!");
        return mReaderHelper1;
    }
    public static ReaderHelper getEpcHelper() throws Exception {
        if (mReaderHelper1 == null || mContext == null)
            throw new NullPointerException("mReaderHelper Or mContext is Null!");
        return mReaderHelper1;
    }

    public static ReaderHelper getInfoHelper() throws Exception {
        if (mReaderHelper2 == null || mContext == null)
            throw new NullPointerException("mReaderHelper Or mContext is Null!");
        return mReaderHelper2;
    }
    /**
     * 设置并返回helper中全局的读写器基类。
     * 
     * @param in 输入流
     * @param out 输出流
     * @return helper中全局的读写器基类
     * @throws Exception 当in或out为空时会抛出错误
     */
    public ReaderBase setReader(InputStream in, OutputStream out)
            throws Exception {

        if (in == null || out == null)
            throw new NullPointerException("in Or out is NULL!");

        if (mReader == null) {
            mReader = new ReaderBase(in, out) {

                @Override
                public void onLostConnect() {
                    if (mListener != null) {
                        mListener.onLostConnect();
                    }
                }

                @Override
                public void analyData(MessageTran msgTran) {
                }

                @Override
                public void reciveData(byte[] btAryReceiveData) {
                    if (mListener != null) {
                        //mListener.onRawInfo(btAryReceiveData);
                        if (btAryReceiveData.length >=Utils.LENGTH_RAW_RFID) {
                            mListener.onEPCInfo(btAryReceiveData);
                        } else if (btAryReceiveData.length ==Utils.LENGTH_RAW_WEIGHT) {
                            mListener.onWeightInfo(btAryReceiveData);
                        }
                    }
                }

                @Override
                public void sendData(byte[] btArySendData) {

                }
            };
        }

        return mReader;
    }

    /**
     * 返回helper中全局的读写器基类。
     * 
     * @return helper中全局的读写器基类
     * @throws Exception
     *             当helper中全局的读写器基类为空时会抛出错误
     */
    public ReaderBase getReader() throws Exception {
        if (mReader == null) {
            throw new NullPointerException("mReader is Null!");
        }

        return mReader;
    }

}