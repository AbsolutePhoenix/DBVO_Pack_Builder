package com.absolutephoenix.dbvopackbuilder.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FtpUtil {

    private String server;
    private int port;
    private String user;
    private String pass;

    public FtpUtil(String server, int port, String user, String pass) {
        this.server = server;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public FTPClient connect() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(server, port);
        ftpClient.login(user, pass);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient;
    }

    public void downloadFile(FTPClient ftpClient, String remoteFilePath, String localFilePath) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(localFilePath)) {
            boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
            if (!success) {
                throw new IOException("File download failed: " + remoteFilePath);
            }
        }
    }

    public void disconnect(FTPClient ftpClient) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                // Handle exception
            }
        }
    }
}
