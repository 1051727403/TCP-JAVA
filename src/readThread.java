import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class readThread implements Runnable{
    public JFrame frame;
    public BufferedReader br;
    public JTextArea textArea;
    public String name;//身份：客户端显示Sever，服务端显示读入的客户端的名字
    //传入文本框以及接收的输入流
    private SocketTCP_Server sever;
    private newSever newSever;
    public readThread(String name,JTextArea textArea,BufferedReader br,JFrame frame){
        this.br=br;
        this.textArea=textArea;
        this.name=name;
        this.frame=frame;
    }
    //服务端读取，若客户端异常退出，则可用这个更新用户列表
    public readThread(String name,JTextArea textArea,BufferedReader br,JFrame frame,SocketTCP_Server sever,newSever newSever){
        this.br=br;
        this.textArea=textArea;
        this.name=name;
        this.frame=frame;
        this.sever=sever;
        this.newSever=newSever;
    }
    //重写run函数
    public void run(){
        try {
            String info;
            while((info=br.readLine())!=null){
                System.out.println("读取数据"+info);
                //客户端
                if(this.name.equals("Sever")) {
                    textArea.append("【服务器】："+info+"\n");
                    //服务器强制断开连接
                    if(info.equals("#exit")){
                        textArea.append("【服务器】：已强制断开与本机的连接!\n");
                        frame.dispose();
                        //弹窗断开连接字样
                        JFrame jf = new JFrame();
                        jf.setTitle("强制下线通知");
                        jf.setLocation(700, 400);
                        jf.setSize(400, 100);
                        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //退出方式
                        jf.setResizable(false);
                        jf.setLocationRelativeTo(null);     //居中显示
                        jf.setLayout(new FlowLayout(1));  //设置窗体布局为流布局
                        jf.setVisible(true);
                        //设置标签
                        JLabel labLoginFail = new JLabel("服务器强制断开了与客户端的连接！");
                        jf.add(labLoginFail);
                        //所有组件可视化
                        jf.setVisible(true);
                    }
                }else{
                    String dateTime=sever.getData();
                    String[] totalInfo=info.split(":");
                    System.out.println("两个客户端之间发送消息"+name+"->"+totalInfo[0]);
                    System.out.println("发送消息有"+totalInfo.length+"段");
                    if(totalInfo.length!=2) {
                        //向服务器发送消息
                        this.textArea.append("【服务器】：" + dateTime + "  用户名：" + name + "   发送消息：" + info + "\n");
                        System.out.println("【服务器】：" + dateTime + "  用户名：" + name + "   发送消息：" + info + "\n");
                    }else{
                        //向指定用户发送消息
                        this.textArea.append("【服务器】：" + dateTime + "  用户名：" + name + "   向"+totalInfo[0]+"发送消息：" + totalInfo[1] + "\n");
                        System.out.println("【服务器】：" + dateTime + "  用户名：" + name + "   向"+totalInfo[0]+"发送消息：" + totalInfo[1] + "\n");
                        //将消息发送给指定用户

                        //判断该用户是否登陆
                        String username=totalInfo[0];
                        Socket socketto=null;
                        Socket socketfrom=this.newSever.socket;
                        //从已登录用户中判断该用户是否存在
                        boolean logined = false;
                        for (int i = 0; i < sever.loginedUserList.size(); i++) {
                            LoginedUserList user = sever.loginedUserList.get(i);
                            if (username.equals(user.username)) {
                                logined = true;
                                socketto = sever.mp.get(username);
                                break;
                            }
                        }
                        if (logined == false) {
                            //未登录，返回提示用户
                            OutputStream os = socketfrom.getOutputStream();
                            String notlogined="该用户未登录，发送消息失败！";
                            System.out.println(notlogined);
                            os.write((notlogined + "\r\n").getBytes());
                            sever.textArea.append("【服务器】：用户 " + username + "未登录，消息发送失败！\r\n");
                        } else {
                            //发送信息到客户端并显示在上方框
                            OutputStream os = socketto.getOutputStream();
                            String sendmessage=name+":"+totalInfo[1];
                            os.write((sendmessage + "\r\n").getBytes());
                            sever.textArea.append("【服务器】："+name+"发送消息到用户 " + username + ":" + totalInfo[1] + "\r\n");
                        }


                    }
                }
            }
        } catch (IOException e) {
            //throw new RuntimeException(e);
            //客户端点击右上角退出
            String username=this.name;
            System.out.println("exit:"+newSever.exit);
            if(username!="Sever"&&newSever.exit==false){
                String dateTime=sever.getData();
                this.textArea.append("【服务器】：" + dateTime + "  用户名：" + username +"  已断开连接\n");
                //客户端主动断开连接，更新服务端
                //遍历userlist，将所有关联的组件删去
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
                //用户断开连接，将用户删去，并减少人数
                sever.cnt= sever.cnt-1;
                sever.personNum.setText("当前服务器在线人数："+sever.cnt);
                sever.loginedUserList.remove(removeUserIdx);
            }

        }
    }
}
