import javax.swing.*;

public class LoginedUserList {
    public String username;
    public JLabel labUserName;
    public JButton kick;
    public newSever newSever;
    LoginedUserList(String username,JLabel labUserName,JButton kick,newSever newSever){
        this.username=username;
        this.labUserName=labUserName;
        this.kick=kick;
        this.newSever=newSever;
    }
}
