package com.example.m1project;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class CustomAlertDialog {
    private Context context;

    public CustomAlertDialog(Context context) {
        this.context = context;
    }

    public void showDialog(String videoId) {
        // Создаем билдера для диалога
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        // Загружаем layout для диалога
        View dialogView = inflater.inflate(R.layout.dialog_layout, null);

        builder.setView(dialogView);


}
