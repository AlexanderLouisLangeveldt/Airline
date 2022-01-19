package airlineserver;

// All imports.
import java.sql.*;
import java.io.*;
import javax.swing.JOptionPane;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AirlineServer {
    //
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStreamReader inputStreamReader;
    private static BufferedReader bufferedReader;
    private static ObjectOutputStream output;
    private static ObjectInputStream input;
    private static Connection connect;
    private static ResultSet resultSet;
    private static Statement statement;
    private static String regName;
    private static String regSname;
    private static String regUser;
    private static String regEmail;
    private static String regPassword;

    AirlineServer() {
        try {
            // Server port and setting inputs and outputs.
            serverSocket = new ServerSocket(7777);
            clientSocket = serverSocket.accept();
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());
            int check;
            // Making a do while loop with a switch case statement for application server operations.
            do {
                check = (int) input.readObject();
                switch (check) {
                    case 0:
                        // Login case
                        System.out.println("Login");
                        output.writeObject(login_read((String) input.readObject(), (String) input.readObject()));
                        output.flush();
                        break;
                    case 1:
                        // Register case
                        System.out.println("Registration");
                        output.writeObject(reg_write((String) input.readObject(), (String) input.readObject(), (String) input.readObject(), (String) input.readObject(), (String) input.readObject()));
                        output.flush();
                        break;
                    case 2:
                        System.out.println("Flights");
                        break;
                    default:
                        break;
                }
            } while (check != 10);
            {

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirlineServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean login_read(String user_email, String user_pass) {
        // Boolean used for reading from the database.
        boolean login_read_bool = false;
        // try catch to wright to database.
        try {
            PreparedStatement prepState_log1;
            ResultSet resultSet_log;
            String sql = "SELECT useremail, userpassword FROM users WHERE useremail = ? AND userpassword = ?";
            prepState_log1 = connect.prepareStatement(sql);
            prepState_log1.setString(1, user_email);
            prepState_log1.setString(2, user_pass);
            resultSet_log = prepState_log1.executeQuery();
            resultSet_log.next();
            String checkUser = resultSet_log.getString(1);
            System.out.println(checkUser);
            String checkPassword = resultSet_log.getString(2);
            if ((checkUser.equals(user_email)) && (checkPassword.equals(user_pass))) {
                login_read_bool = true;
                System.out.println("loged in succesfully c: ");
            } else {
                login_read_bool = false;
                System.out.println("login failed :c ");
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return login_read_bool;
    }
    
    private boolean reg_write(String user_name, String user_sname, String user_username, String user_email, String user_pass) {
        // boolean used for writing all colected information to the database.
        boolean reg_write_bool = false;
        // try catch to wright to database.
        try {
            PreparedStatement prepState_reg1;
            ResultSet resultSet1;
            String sql_reg1 = "SELECT useremail FROM users WHERE useremail = ?";
            prepState_reg1 = connect.prepareStatement(sql_reg1);
            prepState_reg1.setString(1, user_email);
            resultSet1 = prepState_reg1.executeQuery();
            if (resultSet1.next() == false) {
                System.out.println("good so far");
                reg_write_bool = true;
                PreparedStatement prepState_reg2;
                String sql_reg2 = "INSERT INTO users (username, useremail, userpassword) VALUES (?,?,?)";
                prepState_reg2 = connect.prepareStatement(sql_reg2);
                prepState_reg2.setString(1, user_name);
                prepState_reg2.setString(2, user_sname);
                prepState_reg2.setString(3, user_username);
                prepState_reg2.setString(4, user_email);
                prepState_reg2.setString(5, user_pass);
                prepState_reg2.executeUpdate();
                System.out.println("registration succesfull!");
            } else {
                reg_write_bool = false;
            }
        } catch (SQLException e) {
            System.err.println(e);
        }
        return reg_write_bool;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        // Connecting to database.
        try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/airline_database", "root", "");
            System.out.println("Database, connected! \n");
        } catch (SQLException e) {
            System.out.println("Database, not connected :c \n");
        }
        AirlineServer myAirlineServer = new AirlineServer();
    }
}