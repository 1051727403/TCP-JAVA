import javax.swing.*;
import java.awt.*;
import java.awt.image.LookupTable;
import java.io.*;
import java.net.Socket;

public class newSever implements Runnable{
    public boolean exit=false;
    public Socket socket;
    public JTextArea textArea;
    public JTextArea sendMessage;
    public SocketTCP_Server sever;
    public int cnt;
    newSever(Socket socket,JTextArea textArea,SocketTCP_Server sever,int cnt,JTextArea sendMessage){
        this.socket=socket;
        this.textArea=textArea;
        this.sever=sever;
        this.cnt=cnt;
        this.sendMessage=sendMessage;
    }
    public void run(){
        try {
            SocketTCP_Server sever=this.sever;
            Socket socket=this.socket;
            JTextArea textArea=this.textArea;
            InputStream is=null;
            OutputStream os=null;
            //3. 通过socket.getInputStream() 读取客户端写入到数据通道的数据，此处读取到的是字节流
            is = socket.getInputStream();
            //4. IO读取
            //字节流通过br转化为字符流，提高读取效率。
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = br.readLine();
            //读取时间
            String dateTime = sever.getData();
            System.out.println("【服务器】：" + dateTime + "  " + info);
            textArea.append("【服务器】：" + dateTime + "  " + info+"\n");
            //处理出用户名和密码
            String username = null;
            String password = null;
            for (int i = 4; i < info.length(); i++) {
                if (info.charAt(i) == '：') {
                    username = info.substring(4, i - 4);
                    password = info.substring(i + 1, info.length() - 12);
                    break;
                }
            }
            boolean loginSuccess=false;
            //与库中用户信息进行对比
            if(sever.userList.containsKey(username)){
                if(sever.userList.get(username).equals(password)){
                    loginSuccess=true;
                }else{
                    loginSuccess=false;
                }
                System.out.println(sever.userList.containsKey(username));
                System.out.println(sever.userList.get(username).equals(password));
                System.out.println(username);
                System.out.println(password);
            }else{
                loginSuccess=false;
            }

            //检测用户账号密码，并进行返回
            dateTime = sever.getData();
            if (loginSuccess) {
                sever.cnt=sever.cnt+1;
                sever.personNum.setText("当前服务器在线人数："+sever.cnt);
                System.out.println("【服务器】：" + dateTime + "  用户名：" + username + "  密码：" + password + "  登录服务器成功！\r\n");
                sever.textArea.append("【服务器】：" + dateTime + "  用户名：" + username + "  密码：" + password + "  登录服务器成功！\r\n");
                //将该人添加入hashmap
                sever.mp.put(username,socket);
                //服务器发送给客户端数据true，代表用户登陆成功
//                Thread th=new Thread(new sendThread(socket,"true"));
//                th.start();
                os=socket.getOutputStream();
                os.write(("true"+"\r\n").getBytes());
                //将用户显示在服务器已登录窗口配一个按钮

                JLabel labUserName=new JLabel("   用户名："+username);
                labUserName.setBounds(460,30+sever.cnt*40,145,30);
                labUserName.setOpaque(true);
                labUserName.setBackground(Color.white);
                sever.frame.add(labUserName);
                //添加踢出按钮
                JButton kick=new JButton("强制下线");
                kick.setBounds(615,30+sever.cnt*40,100,30);
                KickUserButton kickUserButton=new KickUserButton(sever.frame,sever);
                kick.addActionListener(kickUserButton);
                kick.setActionCommand(username);

                sever.frame.add(kick);
                sever.frame.repaint();
                //将该标签和按钮加入数组
                sever.loginedUserList.add(new LoginedUserList(username,labUserName,kick,this));
                //持续接收客户端消息
                Thread read=new Thread(new readThread(username,textArea,br,sever.frame,sever,this));
                read.start();

            } else {
                dateTime=sever.getData();
                System.out.println("【服务器】：" + dateTime + "  用户名：" + username + "  密码：" + password + "  登录服务器失败！");
                textArea.append("【服务器】：" + dateTime + "  用户名：" + username + "  密码：" + password + "  登录服务器失败！\n");
                //服务器发送给客户端数据false，代表用户登陆成功
                os = socket.getOutputStream();
                os.write("false\r\n".getBytes());
                //关闭IO流和socket
                is.close();
                os.close();
                socket.close();
                dateTime=sever.getData();
                this.exit=true;
                textArea.append("【服务器】："+ dateTime + "  用户名：" + username+"  已断开连接\n");
                System.out.println("【服务器】："+ dateTime + "  用户名：" + username+"  已断开连接\n");
            }

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}