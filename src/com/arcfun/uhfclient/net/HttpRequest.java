package com.arcfun.uhfclient.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.arcfun.uhfclient.utils.LogUtils;

public class HttpRequest {
    private static final String TAG = "zxz|http";
    public static final String URL_HEAD = "http://arcfun.0lz.net/public/";
    public static final String GET_SEC_GOOD = "api/common/getSecGoods.html";
    public static final String GET_SLIDE_LIST = "api/common/getSlideList.html";
    public static final String ADD_ORDER = "api/order/addOrder.html";
    public static final String GET_CUSTOMER_BY_RFIDS = "api/common/getCustomerByRfids.html";

    public static final String USER_LOGIN = "api/user/login.html";
    public static final String USER_ADD = "api/user/add.html";
    public static final String USER_GET_ORDER = "api/user/getOrders.html";
    public static final String USER_GET_PRE = "api/user/getPreOrders.html";
    public static final String USER_GET_AREA = "api/common/getArea.html";
    public static final String USER_GET_ALL_MOBILE = "api/user/getAllMobile.html";

    public static final String BIND_PRE_ORDER = "api/user/bindPreOrder.html";
    public static final String UNBIND_PRE_ORDER = "api/user/unbindPreOrder.html";

    public static final String SET_PUSH_CODE = "api/common/setPushCode.html";
    public static final String DEVICE_LOGIN = "api/machine/login.html";
    public static final String GET_QR_CODE = "api/machine/getQRcode.html";
    public static final String MACHINE_LOGIN = "api/machine/userLogin.html";
    public static final String MACHINE_SET_STATE = "api/machine/setState.html";

    /**
     * 向指定 URL 发送POST方法的请求
     * @param url 发送请求的 URL
     * @param param 请求参数
     * @return 所代表远程资源的响应结果
     */
    public static byte[] sendPost(String urlPath, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        HttpURLConnection conn = null;
        byte[] result = null;
        try {
            URL url = new URL(urlPath);
            conn = (HttpURLConnection) url.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setConnectTimeout(5000);// 设置连接超时,5000ms
            conn.setReadTimeout(5000);// 设置指定时间内服务器没有返回数据的超时，5000ms
            conn.setDoOutput(true);// 设置允许输出
            conn.setDoInput(true);// 设置请求的方式
            conn.setRequestMethod("POST");

            if (param != null  && !param.isEmpty()) {
                byte[] writebytes = param.getBytes("UTF-8");
                // 设置文件长度
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(writebytes);
                outwritestream.flush();
                outwritestream.close();
            }

            int response = conn.getResponseCode();
            LogUtils.d(TAG, "sendPost response=" + response + ", " + conn.getDate());
            if(response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = conn.getInputStream();
                result = dealResponseResult(inptStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(conn != null){
                    conn.disconnect();
                }
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private static byte[] dealResponseResult(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024*4];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap getImageFromServer(String path) {
        try {
            URL url = new URL(path);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setRequestMethod("POST"); // 默认是get请求，
            connection.setConnectTimeout(5000); // 设置访问超时的时间。
            if (200 == connection.getResponseCode()) { // 获取响应码
                String type = connection.getContentType();
                int length = connection.getContentLength();
                LogUtils.i(TAG, "type = " + type + ", length = " + length);
                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}