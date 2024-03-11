package com.android.megainventory;

public class FtpServerErrorListener implements ErrorListener {
    private BuiltinFtpServer builtinFtpServer = null; //!< The builtin ftp server instance. Chen xin.

    @Override
    public void onError(Integer errorCode) {
        builtinFtpServer.onError(errorCode); // Report error.
    }

    public FtpServerErrorListener(BuiltinFtpServer builtinFtpServer) {
        this.builtinFtpServer = builtinFtpServer;

    } //public FtpServerErrorListener(BuiltinFtpServer builtinFtpServer)
}