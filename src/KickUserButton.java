import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.CheckedOutputStream;

public class KickUserButton implements ActionListener {
    public SocketTCP_Server sever;
    public JFrame frame;
    public newSever newSever;
    KickUserButton(JFrame frame,SocketTCP_Server sever){
        this.sever=sever;
        this.frame=frame;
    }
    public void actionPerformed(ActionEvent e) {
        try {
            String username=e.getActionCommand();
            System.out.println(username);
            System.out.println("当前登录用户数量："+sever.loginedUserList.size());
            int cnt=0;
            //遍历userlist
            int removeUserIdx=0;
            for(int i=0;i<sever.loginedUserList.size();i++){
                LoginedUserList user=sever.loginedUserList.get(i);
                if(user.username.equals(username)){
                    removeUserIdx=i;
                    frame.remove(user.labUserName);
                    frame.remove(user.kick);
                    System.out.println("跳过组件名称："+user.username);
                }else{
                    System.out.println("添加组件名称："+user.username);
                    cnt++;
                    //重画组件,将所有组件的位置进行调整
                    user.labUserName.setBounds(460,30+cnt*40,145,30);
                    user.kick.setBounds(615,30+cnt*40,100,30);
                }
            }

            //重画
            frame.repaint(1);
            //获取服务器线程
            newSever newSever=sever.loginedUserList.get(removeUserIdx).newSever;
            //用户断开连接，将用户删去，并减少人数
            sever.cnt= sever.cnt-1;
            sever.personNum.setText("当前服务器在线人数："+sever.cnt);
            sever.loginedUserList.remove(removeUserIdx);
            //获取该用户的socket
            Socket socket=sever.mp.get(username);
            //向客户端发送下线信息
            OutputStream os = socket.getOutputStream();
            os.write(("#exit\r\n").getBytes());
            String dataTime=sever.getData();
            sever.textArea.append("【服务器】："+dataTime+" 用户名："+username+" 已断开连接！\r\n");
            //关闭socket
            socket.close();
            //从mp中删去
            sever.mp.remove(username);
            //关闭服务端线程
            newSever.exit=true;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
