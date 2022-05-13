package com.aiyouwei.drk.shelf.admin;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.aiyouwei.drk.shelf.R;

public class EmployeeViewHolder extends RecyclerView.ViewHolder {

    public TextView idText, nameText, faceText, editBtn, deleteBtn;

    public EmployeeViewHolder(View itemView) {
        super(itemView);
        idText = (TextView) itemView.findViewById(R.id.user_id);
        nameText = (TextView) itemView.findViewById(R.id.username);
        faceText = (TextView) itemView.findViewById(R.id.face_data);
        editBtn = (TextView) itemView.findViewById(R.id.btn_edit);
        deleteBtn = (TextView) itemView.findViewById(R.id.btn_delete);
    }
}
