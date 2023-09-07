import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * 客户端
 */
public class SocketTCP_Client {
    public static void main(String[] args) throws Exception {
        SocketTCP_Client sever = new SocketTCP_Client();
        sever.showMenu();
    }
    public void showMenu(){
        JFrame frame = new JFrame("SocketTCP_Client");
        frame.setLocation(700,100);
        frame.setSize(700,500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //退出方式
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);     //居中显示
        frame.setLayout(new FlowLayout(1));  //设置窗体布局为流布局
        frame.setVisible(true);
        //创建一个客户端标签
        JLabel labClient=new JLabel("Client客户端");
        labClient.setFont(new Font("", Font.PLAIN, 30)); // 设置文字的字体及大小
        frame.add(labClient);
        //换行
        frame.add(new JLabel("                                                                       " +
                "                                                                                               " +
                "                                                                                                "));
        //创建一个账号标签
        JLabel labUsername=new JLabel("账号:");
        labUsername.setFont(new Font("", Font.PLAIN, 20)); // 设置文字的字体及大小
        frame.add(labUsername);
        //创建一个账号输入框
        JTextField textUsername=new JTextField();
        Dimension dimsize = new Dimension (420,30);
        textUsername.setPreferredSize(dimsize);
        frame.add(textUsername);
        //换行
        frame.add(new JLabel("                                                                       " +
                "                                                                                               " +
                "                                                                                                "));
        //创建一个密码标签
        JLabel labPassword=new JLabel("密码:");
        labPassword.setFont(new Font("", Font.PLAIN, 20)); // 设置文字的字体及大小
        frame.add(labPassword);
        //创建一个密码输入框
        JTextField textPassword=new JTextField();
        textPassword.setPreferredSize(dimsize);
        frame.add(textPassword);
        //换行
        frame.add(new JLabel("                                                                       " +
                "                                                                                               " +
                "                                                                                                "));
        //创建一个登录按钮
        JButton login=new JButton("登录");
        ButListener butListener=new ButListener(frame,textUsername,textPassword);
        login.addActionListener(butListener);
        frame.add(login);
        //可视化所有组件
        frame.setVisible(true);
    }
}