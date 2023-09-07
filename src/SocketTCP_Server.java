import org.w3c.dom.Text;

import javax.security.auth.kerberos.KerberosTicket;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.zip.CheckedOutputStream;

/**
 服务器端
 */
public class SocketTCP_Server {
    public JFrame frame;
    public JTextArea textArea;
    public JTextArea sendMessage;
    public JLabel personNum;
    public int cnt=0;
    public ArrayList<LoginedUserList> loginedUserList=new ArrayList<LoginedUserList>();
    public HashMap<String,Socket>mp=new HashMap<String,Socket>();
    public HashMap<String,String>userList=new HashMap<String,String>();
    public String getData(){
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
    //利用绝对布局实现
    public void showMenu(SocketTCP_Server sever){
        JFrame frame = new JFrame("SocketTCP_Sever");
        this.frame=frame;
        frame.setLocation(100,100);
        frame.setSize(750,750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //退出方式
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);     //居中显示
        frame.setLayout(null);  //设置窗体布局为绝对布局
        //当前在线人数显示
        JLabel personNum=new JLabel("当前服务器在线人数：0");
        personNum.setBounds(460,10,200,40);
        this.personNum=personNum;
        frame.add(personNum);
        //创建一个服务器基础信息展示框
        JLabel basicInfo=new JLabel("                     服务器IP地址：127.0.0.1    服务器端口号：9999");
        basicInfo.setBounds(20,10,420,40);
        basicInfo.setOpaque(true);
        basicInfo.setBackground(Color.white);
        frame.add(basicInfo);
        //创建一个服务器信息显示标签
        JLabel message=new JLabel("服务器信息显示框：\n");
        message.setBounds(20,50,200,30);
        frame.add(message);
        //创建一个文本框存放消息
        JTextArea textArea=new JTextArea(20,30);
        textArea.setBounds(20,80,420,450);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(false);
        frame.add(textArea);
        this.textArea=textArea;
        // 服务器发送信息框
        //创建一个服务器发送信息显示标签
        JLabel labSendMessage=new JLabel("服务器发送信息框（规则：username  :  info）：\n");
        labSendMessage.setBounds(20,530,300,30);
        frame.add(labSendMessage);
        //创建一个文本框存放将要发送的消息
        JTextArea jtSendMessage=new JTextArea(20,30);
        jtSendMessage.setBounds(20,560,420,100);
        jtSendMessage.setLineWrap(true);
        jtSendMessage.setWrapStyleWord(false);
        frame.add(jtSendMessage);
        //创建一个发送按钮
        //消息发送按钮
        JButton sendMessageButton=new JButton("发送消息");
        sendMessageButton.setBounds(160,670,100,30);
        SendMessageSeveer sendMessageSever=new SendMessageSeveer(sever,textArea,jtSendMessage);
        sendMessageButton.addActionListener(sendMessageSever);
        frame.add(sendMessageButton);
        this.sendMessage=jtSendMessage;
        //组件可视化
        frame.setVisible(true);
    }
    public static void main(String[] args) throws Exception {
        SocketTCP_Server sever=new SocketTCP_Server();
        //1. 在本机 的9999端口监听, 等待连接
        ServerSocket serverSocket = new ServerSocket(9999);
        sever.showMenu(sever);
        System.out.println("【服务器】：监听9999端口，等待连接...");
        sever.textArea.append("【服务器】：监听9999端口，等待连接...\n");
        //2. 当没有客户端连接9999端口时，程序会 阻塞, 等待连接
        //导入文本中用户密码信息
        String path=SocketTCP_Server.class.getClassLoader().getResource("").getPath()+"text.txt";
        File myFile = new File(path);
        if (myFile.isFile() && myFile.exists()) {
            InputStreamReader Reader = new InputStreamReader(new FileInputStream(myFile), StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(Reader);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                String []str=lineTxt.split(" ");
                sever.userList.put(str[0],str[1]);
                System.out.println(str[0]+"  "+str[1]);
            }

            Reader.close();
        }
//        已登录的客户端数量
        sever.cnt=0;
        while (true) {
            Socket socket = serverSocket.accept();
            Thread newSeverTcp=new Thread(new newSever(socket,sever.textArea,sever,sever.cnt,sever.sendMessage));
            newSeverTcp.start();
        }
    }

}
