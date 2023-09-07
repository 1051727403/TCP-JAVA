import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.zip.CheckedOutputStream;

import javax.swing.*;

//按钮监听器的父类ActionListener里面有函数可以直接检测按钮是否被点击
public class ButListener implements ActionListener {
    //定义JTextFieldb变量jt,用来保存传递过来的文本框对象
    private JTextField username;
    private JTextField password;
    private JFrame loginMenu;

    //重写构造函数
    public ButListener(JFrame loginMenu,JTextField username,JTextField password){
        this.username=username;
        this.password=password;
        this.loginMenu=loginMenu;
    }
    public void messageMenu(Socket socket) throws IOException {
        //隐藏登录窗口
        this.loginMenu.dispose();

        //跳转到新的页面
        JFrame frame=new JFrame();
        frame.setTitle("Client登陆成功发送信息页面！！");
        frame.setLocation(700,100);
        frame.setSize(500, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //退出方式
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);     //居中显示
        frame.setLayout(new FlowLayout(1));  //设置窗体布局为流布局
        frame.setVisible(true);
        //消息接收标签
        JLabel labReceive=new JLabel("发送信息展示及反馈窗口");
        frame.add(labReceive);
        frame.add(new JLabel("                                                                          " ));
        //消息接收窗口：
        JTextArea textArea=new JTextArea(18,45);
        textArea.setSize(400,380);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(false);
        textArea.append("【服务器】：用户 "+this.username.getText()+"  登陆成功！\n");
        frame.add(textArea);
        //消息发送标签
        JLabel labSend=new JLabel("信息发送窗口");
        frame.add(labSend);
        frame.add(new JLabel("                                                                                                           " ));
        //消息接收窗口：
        JTextArea textSend=new JTextArea(8,45);
        textSend.setSize(400,180);
        textSend.setLineWrap(true);
        textSend.setWrapStyleWord(true);
        frame.add(textSend);
        //消息发送按钮
        JButton sendMessage=new JButton("发送消息(格式：发送对象的用户名：发送的消息)");
        String username=this.username.getText();
        SendButtonListener sendButtonListener=new SendButtonListener(socket,frame,textArea,textSend,username);
        sendMessage.addActionListener(sendButtonListener);
        frame.add(sendMessage);
        //组件可视化
        frame.setVisible(true);

        //登录成功，展示页面的同时新开一个读取服务端的线程
        InputStream is = socket.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        new Thread(new readThread("Sever",textArea,br,frame)).start();


        //弹窗登陆成功字样
        JFrame jf = new JFrame();
        jf.setTitle("登陆成功！");
        jf.setLocation(700, 400);
        jf.setSize(400, 100);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);    //退出方式
        jf.setResizable(false);
        jf.setLocationRelativeTo(null);     //居中显示
        jf.setLayout(new FlowLayout(1));  //设置窗体布局为流布局
        jf.setVisible(true);
        //设置标签
        JLabel labLoginFail = new JLabel("登陆成功！");
        jf.add(labLoginFail);
        //所有组件可视化
        jf.setVisible(true);
    }
    public void actionPerformed(ActionEvent e){
        try {
            Socket socket=new Socket(InetAddress.getLocalHost(),9999);
            System.out.println(this.username.getText()+"\n"+this.password.getText());
            //1. 连接服务端 (ip , 端口）
            //连接本机的 9999端口, 如果连接成功，返回Socket对象

            //2.客户端用户输入密码，若正确则跳出循环
            //可视化界面，用户输入用户名密码进行登录，用socket接口向服务器发送账号密码进行验证，通过返回值进行页面跳转。
            String username = this.username.getText();
            String password = this.password.getText();
            //2.1 输入用户名密码后，生成Socket, 通过socket.getOutputStream()得到和 socket对象关联的输出流对象
            OutputStream os = socket.getOutputStream();
            os.write(("用户名：" + username + "  密码：" + password + "  正在登录服务器...\r\n").getBytes());
            //关闭输出流！关键！若不关闭，则服务器端无法读取流中的内容，同一时间只能一个端口进行读写！错了好久
            //2.2通过输出流，写入数据到数据通道,发送给服务器端进行身份验证
            //若服务器返回“true”，则登陆成功，跳转到发送文件页面。
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String info = br.readLine();
            System.out.println(info);
            if (info.equals("true")) {
                System.out.println("登陆成功！");
                //登陆成功展示新窗口，新的发送按钮作位输入服务端的线程
                this.messageMenu(socket);
            } else {
                System.out.println("登陆失败！用户名或密码错误！");
                //弹窗登陆失败字样
                JFrame jf = new JFrame();
                jf.setTitle("用户名密码错误，请重试！！");
                jf.setLocation(700, 400);
                jf.setSize(400, 100);
                jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);    //退出方式
                jf.setResizable(false);
                jf.setLocationRelativeTo(null);     //居中显示
                jf.setLayout(new FlowLayout(1));  //设置窗体布局为流布局
                jf.setVisible(true);
                //设置标签
                JLabel labLoginFail = new JLabel("登陆失败！用户名密码错误，请重试！！");
                jf.add(labLoginFail);
                //所有组件可视化
                jf.setVisible(true);
                //关闭流
                os.close();
                is.close();
                br.close();
            }




        }catch(IOException ex){
            ex.printStackTrace();
        }
    }
}