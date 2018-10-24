package com.reader.helper;

import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

import com.arcfun.uhfclient.utils.Constancts;
import com.reader.base.ReaderBase;

public class ReaderHelper {
    private ReaderBase mReader;

    private static ReaderHelper mReaderHelper1, mReaderHelper2;

    private Listener mListener = null;
    private int mType;

    public interface Listener {
        public void onLostConnect(int type);

        public void onRawInfo(int type, byte[] btData);// raw

        public void onDEVInfo(int type, byte[] btData);// dev

        public void onEPCInfo(int type, byte[] btData);// epc
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public ReaderHelper() {
    }

    public ReaderHelper(Listener listener) {
        mListener = listener;
    }

    public static void setContext(Context context) throws Exception {
        mReaderHelper1 = new ReaderHelper();
        mReaderHelper2 = new ReaderHelper();
    }

    public static ReaderHelper getDefaultHelper() throws Exception {
        if (mReaderHelper1 == null)
            throw new NullPointerException("mReaderHelper Or mContext is Null!");
        return mReaderHelper1;
    }

    public static ReaderHelper getDevHelper() throws Exception {
        if (mReaderHelper1 == null)
            throw new NullPointerException("mReaderHelper Or mContext is Null!");
        return mReaderHelper1;
    }

    public static ReaderHelper getEpcHelper() throws Exception {
        if (mReaderHelper2 == null)
            throw new NullPointerException("mReaderHelper Or mContext is Null!");
        return mReaderHelper2;
    }

    public ReaderBase setReader(InputStream in, OutputStream out, final int type)
            throws Exception {
        mType = type;

        if (in == null || out == null)
            throw new NullPointerException("in Or out is NULL!");

        if (mReader == null) {
            mReader = new ReaderBase(in, out) {

                @Override
                public void onLostConnect() {
                    if (mListener != null) {
                        mListener.onLostConnect(type);
                    }
                }

                @Override
                public void analyData(byte[] btData) {
                    if (mListener != null) {
                        if (mType == Constancts.TYPE_DEV) {
                            mListener.onDEVInfo(type, btData);
                        } else if (mType == Constancts.TYPE_EPC) {
                            mListener.onEPCInfo(type, btData);
                        }
                    }
                }

                @Override
                public void reciveData(byte[] btAryReceiveData) {
                    if (mListener != null) {
                        if (mType == Constancts.TYPE_DEV &&
                                btAryReceiveData.length == 10) {
                            mListener.onDEVInfo(11, btAryReceiveData);
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

    public ReaderBase getReader() throws Exception {
        if (mReader == null) {
            throw new NullPointerException("mReader is Null!");
        }
        return mReader;
    }

}