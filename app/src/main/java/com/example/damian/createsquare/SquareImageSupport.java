package com.example.damian.createsquare;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.inputmethod.BaseInputConnection;

/**
 * Created by Damian on 2016-10-01.
 */

public class SquareImageSupport extends AsyncTask<Bitmap, Void, Bitmap> {

    private OnCompleteListener onCompleteListener;

    public interface OnCompleteListener {
        Bitmap doingBockgraund(final Bitmap bitmap);
        void onNewImageComplete(final Bitmap bitmap);
    }

    public SquareImageSupport(final OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Bitmap doInBackground(final Bitmap... params) {

        return onCompleteListener.doingBockgraund(params[0]);
    }

    @Override
    protected void onPostExecute(final Bitmap bitmap) {
        super.onPostExecute(bitmap);
        onCompleteListener.onNewImageComplete(bitmap);
    }
}
