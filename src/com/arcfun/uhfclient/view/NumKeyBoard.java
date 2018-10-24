package com.arcfun.uhfclient.view;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.arcfun.uhfclient.R;

public class NumKeyBoard extends LinearLayout {
    public interface OnInputDoneListener {
        /** -1 error; 0 input; 1 finish */
        public void onInput(int type);
    }

    private EditText mEditText, mText;
    private OnInputDoneListener mListener;
    private static final int PHONE_LENGTH = 15;
    private boolean isPassword = false;

    public boolean isPassword() {
        return isPassword;
    }

    public void setPassword(boolean isPassword) {
        this.isPassword = isPassword;
    }

    public String getInput() {
        return mText != null ? mText.getText().toString() : "";
    }

    public NumKeyBoard(Context context) {
        this(context, null);
    }

    public NumKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.ahs_keyborad_layout,
                this, true);

        KeyboardView kv = (KeyboardView) findViewById(R.id.input_keybord);
        Keyboard kb = new Keyboard(context, R.layout.ahs_keyborad);

        kv.setKeyboard(kb);
        kv.setEnabled(true);
        kv.setOnKeyboardActionListener(mActionListener);
        mText = new EditText(context);
    }

    public void setEditText(EditText et, OnInputDoneListener listener) {
        this.mEditText = et;
        this.mListener = listener;
    }

    public EditText getEditText() {
        return mEditText;
    }

    private KeyboardView.OnKeyboardActionListener mActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            if (mEditText != null) {
                Editable editable = mEditText.getText();
                Editable text = mText.getText();
                int selectPos = mEditText.getSelectionStart();

                if (primaryCode == Keyboard.KEYCODE_DONE) {
                    if (mListener != null) {
                        mListener.onInput(1);
                    }
                } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                    if (mListener != null) {
                        mListener.onInput(0);
                    }
                    if (editable != null && editable.length() > 0) {
                        if (selectPos > 0) {
                            editable.delete(selectPos - 1, selectPos);
                        }
                    }
                    if (text != null && text.length() > 0) {
                        if (selectPos > 0) {
                            text.delete(selectPos - 1, selectPos);
                        }
                    }
                } else {
                    if (editable != null && editable.length() < PHONE_LENGTH) {
                        if (mListener != null) {
                            mListener.onInput(0);
                        }
                        if (isPassword) {
                            editable.insert(selectPos, "*");
                        } else {
                            editable.insert(selectPos,
                                    Character.toString((char) primaryCode));
                        }
                        if (text != null) {
                            text.insert(selectPos,
                                    Character.toString((char) primaryCode));
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onInput(-1);
                        }
                    }
                }
            }
        }
    };
}