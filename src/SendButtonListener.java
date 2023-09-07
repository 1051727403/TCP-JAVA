import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.zip.CheckedOutputStream;

import javax.swing.*;

public class SendButtonListener implements ActionListener {
    private JFrame frame;
    private JTextArea showText;
    private JTextArea sendText;
    private Socket socket;
    private String username;
    public SendButtonListener(Socket socket,JFrame frame,JTextArea showText,JTextArea sendText,String username){
        this.sendText=sendText;
        this.socket=socket;
        this.showText=showText;
        this.frame=frame;
        this.username=username;
    }
    public void actionPerformed(ActionEvent e) {
        try {
            Socket socket=this.socket;
            JFrame frame=this.frame;
            JTextArea showText=this.showText;
            JTextArea sendText=this.sendText;
            //发送信息到服务器并显示在上方框中
            OutputStream os = socket.getOutputStream();
            String info=sendText.getText();
            os.write((info+"\r\n").getBytes());
            System.out.println("发送消息"+info);
            showText.append(this.username+":"+info+"\r\n");
            //清空发送框
            sendText.setText("");
        } catch (IOException ex) {
            //throw new RuntimeException(ex);
            showText.append("【服务器】：服务器已关闭！发送无效！\n");

        }
    }
}
