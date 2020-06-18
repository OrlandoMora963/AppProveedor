package com.example.appproveedorgas;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class BottomSheetDialogTime  extends BottomSheetDialogFragment {
    private BottomSheetDialogTime.BottomSheetListener mListener;

    TextInputEditText et_tiempo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_time_layout,container,false);
        et_tiempo = v.findViewById(R.id.txt_tiempo);
        Button button_enviar_con = v.findViewById(R.id.btn_enviar_confirmacion);
        button_enviar_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.OnBottomClickedTime(et_tiempo.getText().toString());
                dismiss();
            }
        });

        return v;
    }
    public  interface BottomSheetListener{
        void OnBottomClickedTime(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetDialogTime.BottomSheetListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() +" must implement BottomSheetListener");
        }

    }

}
