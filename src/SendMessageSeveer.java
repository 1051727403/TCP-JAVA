import javax.crypto.ShortBufferException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SendMessageSeveer implements ActionListener {
    public SocketTCP_Server sever;
    public JTextArea showText;
    public JTextArea sendText;

    SendMessageSeveer(SocketTCP_Server sever,JTextArea showText,JTextArea sendText){
        this.sendText=sendText;
        this.showText= showText;
        this.sever=sever;
    }
    public void actionPerformed(ActionEvent e) {
        try {
            JTextArea showText = this.showText;
            JTextArea sendText = this.sendText;
            Socket socket = null;
            String info = sendText.getText();//发送的信息
            String username = "";//用户名字
            //从输入中提取出发送对象
            boolean f = false;
            for (int i = 0; i < info.length(); i++) {
                if (info.charAt(i) == ':') {
                    f = true;
                    username = info.substring(0, i);
                    info = info.substring(i+1, info.length());
                    System.out.println(username+" "+info);
                    break;
                }
            }
            if (f == false) {//若不符合规范
                showText.append("【服务器】：向客户端发送消息失败！发送不符合规范！\n");
                //清空发送框
                sendText.setText("");
                return;
            }

            //从已登录用户中判断该用户是否存在
            boolean logined = false;
            for (int i = 0; i < sever.loginedUserList.size(); i++) {
                LoginedUserList user = sever.loginedUserList.get(i);
                if (username.equals(user.username)) {
                    logined = true;
                    socket = sever.mp.get(username);
                    break;
                }
            }
            if (logined == false) {
                showText.append("【服务器】：用户 " + username + "未登录，消息发送失败！\n");
                //清空发送框
                sendText.setText("");
                return;
            } else {
                //发送信息到客户端并显示在上方框
                OutputStream os = socket.getOutputStream();
                os.write((info + "\n").getBytes());
                showText.append("【服务器】：发送消息到用户 " + username + ":" + info + "\r\n");
            }
            //清空发送框
            sendText.setText("");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }


}
