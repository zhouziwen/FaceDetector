/*Copyright ©2015 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package com.aiyouwei.drk.shelf.pick;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aiyouwei.drk.shelf.R;

import static android.text.TextUtils.isEmpty;

public class ActivationCodeDialog extends BaseDialog implements View.OnClickListener {
    private static final String TAG = "AlertDialog";
    private boolean isMin=true;

    /**
     * 自定义Dialog监听器
     */
    public interface OnDialogButtonClickListener {

        /**
         * 点击按钮事件的回调方法
         */
        void onDialogButtonClick(String deviceId);
        void onDialogButtonClick();
    }


    private Context context;
    private String title;
    private OnDialogButtonClickListener listener;
    /**
     * 带监听器参数的构造函数
     */
    public ActivationCodeDialog(Context context, OnDialogButtonClickListener listener, boolean isMin) {
        super( context );

        this.context = context;
        this.listener = listener;
        this.isMin = isMin;
        if (deviceIdEdit!=null) {
            if (isMin) {
                deviceIdEdit.setVisibility(View.GONE);
            } else {
                deviceIdEdit.setVisibility(View.VISIBLE);
            }
        }
    }
    private TextView btn_negative_custom_dialog,btn_positive_custom_dialog;
    private EditText deviceIdEdit;
    private ImageView closeImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.dialog_activation_code );
        setCanceledOnTouchOutside( false );
        btn_negative_custom_dialog = findViewById( R.id.btn_negative_custom_dialog );
        btn_positive_custom_dialog = findViewById( R.id.btn_positive_custom_dialog );
        deviceIdEdit = findViewById( R.id.deviceid_edittext );
        closeImageView = findViewById(R.id.close_imageview );
//        deviceIdEdit.setText( "udefrgt54redswef" );
//        secretEdit.setText( "1169tabw" );
        btn_negative_custom_dialog.setOnClickListener( this );
        btn_positive_custom_dialog.setOnClickListener( this );
        closeImageView.setOnClickListener( this );
        if (isMin) {
            deviceIdEdit.setVisibility(View.GONE);
        } else {
            deviceIdEdit.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.close_imageview||v.getId() == R.id.btn_negative_custom_dialog) {
            dismiss();
        } else {
            if (isMin) {
                listener.onDialogButtonClick();
            } else {
                String deviceId = deviceIdEdit.getText().toString();
                if (isEmpty(deviceId)) {

                    return;
                }
                if (v.getId() == R.id.btn_positive_custom_dialog) {
                    listener.onDialogButtonClick( deviceId );
                }
                dismiss();
            }

        }
    }

}

