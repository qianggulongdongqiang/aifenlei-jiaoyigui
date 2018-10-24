package com.arcfun.uhfclient.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.arcfun.uhfclient.R;
import com.arcfun.uhfclient.data.MessageInfo;
import com.arcfun.uhfclient.data.ResultDataInfo;
import com.arcfun.uhfclient.data.ResultOrderInfo;
import com.arcfun.uhfclient.net.HttpRequest;
import com.arcfun.uhfclient.utils.LogUtils;
import com.arcfun.uhfclient.utils.Utils;

public class PopDialog extends Dialog implements OnClickListener {
    private static final String TAG = "Dialog";
    private ImageView mBackView, mCommitView;
    private MessageInfo mMessageInfo;
    private Context mContext;
    private OnUpdateListener mListeners;

    public PopDialog(Context context, MessageInfo info, OnUpdateListener listener) {
        this(context, info, R.style.PopDialog);
        this.mListeners = listener;
    }

    private PopDialog(Context context, MessageInfo info, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
        this.mMessageInfo = info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_weight_layout);
        mBackView = (ImageView) findViewById(R.id.close);
        mCommitView = (ImageView) findViewById(R.id.dialog_commit);
        mBackView.setOnClickListener(this);
        mCommitView.setOnClickListener(this);

        initView();
    }

    private void initView() {
        TextView name = (TextView) findViewById(R.id.dialog_name);
        TextView phone = (TextView) findViewById(R.id.dialog_phone);
        TextView owner = (TextView) findViewById(R.id.dialog_owner);
        TextView category = (TextView) findViewById(R.id.dialog_category);
        TextView weightTitle = (TextView)  findViewById(R.id.dialog_weight_type); 
        TextView weight = (TextView) findViewById(R.id.dialog_weight);
        TextView credit = (TextView) findViewById(R.id.dialog_credit_income);

        name.setText(mMessageInfo.getUserName());
        phone.setText(Utils.formatPhoneNumber(mMessageInfo.getPhoneNum()));
        owner.setText(mMessageInfo.getManagerId());
        category.setText(mMessageInfo.getCategory());
        credit.setText(String.valueOf(mMessageInfo.getIncome()));
        if (mMessageInfo.getType() == Utils.TRANS_PIECES) {
            weightTitle.setText(mContext.getString(R.string.dialog_pieces));
            weight.setText(String.valueOf(mMessageInfo.getPiece()));
        } else {
            weightTitle.setText(mContext.getString(R.string.dialog_weight));
            weight.setText(mMessageInfo.getWeight().trim());
        }
    }

    private void addOrder(final String json) {
        String url = HttpRequest.URL_HEAD + HttpRequest.ADD_ORDER;
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                byte[] data = HttpRequest.sendPost(params[0], json);
                LogUtils.d(TAG, "addOrder data =" + data);
                if (data == null) {
                    return null;
                }
                String result = new String(data);
                LogUtils.d(TAG, "addOrder:" + result);
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                ResultOrderInfo info = new ResultOrderInfo(0, 0, 0, 0);
                if (result != null) {
                    info = Utils.parseResultData(result);
                }
                createDialog(info);
                if (result != null) {
                    if (mListeners != null) {
                        mListeners.OnUpdate(info.getUserScore());
                    }
                }
            };
        }.execute(url);
    }

    private String buildGoodJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("user_id", String.valueOf(mMessageInfo.getUserId()));

            JSONObject good = new JSONObject();
            good.put("id", String.valueOf(mMessageInfo.getId()));
            good.put("num", (mMessageInfo.getType() == Utils.TRANS_PIECES) ? 
                    mMessageInfo.getPiece() : getWeight(mMessageInfo.getWeight()));
            good.put("name", mMessageInfo.getCategory());
            JSONArray array = new JSONArray();
            array.put(good);

            object.put("goods", array);
            object.put("type", "3");
        } catch (JSONException e) {
            LogUtils.e(TAG, "getJsonData:" + e.toString());
        }
        LogUtils.d(TAG, "getJsonData:" + object.toString());
        return object.toString();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.close:
            dismiss();
            break;
        case R.id.dialog_commit:
            addOrder(buildGoodJson());
            dismiss();
            break;
        default:
            break;
        }
    }

    private void createDialog(ResultOrderInfo result) {
        ResultDataInfo info = new ResultDataInfo(mMessageInfo,
                result.getCode(),
                result.getTimeStamp(), String.valueOf(mMessageInfo.getScore()),
                String.valueOf(result.getPointsnumber()),
                String.valueOf(result.getUserScore()));
        if (info != null) {
            Dialog dialog = new PopResultDialog(mContext, info);
            dialog.show();
        }
    }

    private float getWeight(String data) {
        return Float.valueOf(data.replace(" ", "").split("kg")[0]);
    }
    public interface OnUpdateListener{
        public void OnUpdate(int score);
    }
}