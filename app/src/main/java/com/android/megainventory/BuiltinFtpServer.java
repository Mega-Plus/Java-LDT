package com.android.megainventory;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.stupidbeauty.ftpserver.lib.FtpServer;

import java.net.BindException;


public class BuiltinFtpServer {
    private static final String TAG = "BuiltinFtpServer"; //!< 输出调试信息时使用的标记
    private ErrorListener errorListener = null; //!< Error listener.
    private FtpServerErrorListener ftpServerErrorListener = null; //!< The ftp server error listner. Chen xin.
    private int port = 21143; //!< Port.
    private FtpServer ftpServer = null; //!< Ftp server object.
    private boolean allowActiveMode = false; //!<  Whether to allow active mode.

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    } //public void setErrorListener(ErrorListener errorListener)

    public void onError(Integer errorCode) {
        if (errorListener != null) {
            errorListener.onError(errorCode); // Report error.
        } else // Not listener
        {
//             throw new BindException();
            Exception ex = new BindException();
            throw new RuntimeException(ex);
        }
    } //public void onError(Integer errorCode)

    /**
     * Set to allow or not allow active mode.
     */
    public void setAllowActiveMode(boolean allowActiveMode) {
        this.allowActiveMode = allowActiveMode;
    } //private void setAllowActiveMode(allowActiveMode)

    public void setPort(int port) {
        this.port = port;
    } //public void setPort(int port)

    private BuiltinFtpServer() {
    }

    public BuiltinFtpServer(Context context) {
        this.context = context;
    }

    private Context context; //!< Context.

    public void start() {
        com.stupidbeauty.ftpserver.lib.ErrorListener errorListener = new com.stupidbeauty.ftpserver.lib.ErrorListener() {
            @Override
            public void onError(Integer errorCode) {
                Toast.makeText(context, "ERROR FTP", Toast.LENGTH_SHORT).show();
            }
        };

        ftpServer = new FtpServer("192.168.100.4", port, context, allowActiveMode);
        ftpServer.setErrorListener(errorListener); // Set error listner. Chen xin.
        Log.d(TAG, "start, rootDirectory: " + Environment.getExternalStorageDirectory()); // Debug.

        ftpServer.setRootDirectory(Environment.getExternalStorageDirectory()); // 设置根目录。
    }
}
