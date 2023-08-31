package com.example.UserDemo.Controller;

import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class LogActivity{
    public static void writeFile(String text) throws IOException {
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/user/Desktop/UserDemo/src/main/java/com/example/UserDemo/Controller/Users.txt", true))) {
            bw.write(text);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
@RestController
@RequestMapping("/request/handler")
public class RequestController {
    Comparator<Users> usersComparator = Comparator.comparing(Users::getName);
//    Comparator<Users> usersComparator = (s1, s2) -> s1.getName().compareTo(s2.getName());

    @GetMapping("/user/{name}")
    public Users getUser(@PathVariable("name") String name){

        Users user = new Users();

        try(Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/users_db", "root", "efe@123#");
            Statement statement = connection.createStatement()){

            ResultSet resultSet= statement.executeQuery(String.format("SELECT * FROM users WHERE name= '%s'", name));
            while( resultSet.next() ){
                user.setName(resultSet.getString("name"));
                user.setAge(Integer.parseInt(resultSet.getString("age")));
                user.setAccountBalance(Long.parseLong(resultSet.getString("accountBalance")));
                user.setLocation(resultSet.getString("location"));
            }
            resultSet.close();
            return user;
        } catch( SQLException exception ){
            System.out.println(exception.getMessage());
            return null;
        }

    }
    @GetMapping("/users")
    public List<Users> getAll(){
        List<Users> list = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/users_db", "root", "efe@123#");
            Statement statement = connection.createStatement()){

            ResultSet resultSet= statement.executeQuery("SELECT * FROM users");
            while( resultSet.next() ){
                list.add(new Users(resultSet.getString("name"),
                        Integer.parseInt(resultSet.getString("age")),
                        resultSet.getString("accountBalance"),
                        resultSet.getString("location")) );
            }
            resultSet.close();
            return list;
        } catch( SQLException exception ){
            System.out.println(exception.getMessage());
            return null;
        }

    }
    @GetMapping("/users/sorted")
    public List<Users> getAllSorted(){
        List<Users> list = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/users_db", "root", "efe@123#");
            Statement statement = connection.createStatement()){

            ResultSet resultSet= statement.executeQuery("SELECT * FROM users");
            while( resultSet.next() ){
                list.add(new Users(resultSet.getString("name"), Integer.parseInt(resultSet.getString("age")), resultSet.getString("accountBalance"), resultSet.getString("location")) );
            }
            resultSet.close();
            return list.stream().sorted(usersComparator).toList();
        } catch( SQLException exception ){
            System.out.println(exception.getMessage());
            return null;
        }
    }

    @PostMapping("/users")
    public String postUser(@RequestBody Users user) throws IOException {

        try(Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/users_db", "root", "efe@123#");
            Statement statement = connection.createStatement()) {
            String createTable = "CREATE TABLE IF NOT EXISTS users(name TEXT, age Integer, accountBalance Integer, location TEXT)";
            statement.execute(createTable);

            String insertInfo = String.format("INSERT INTO users(name, age, accountBalance, location) " +
            "VALUES('%s',%d,%d,'%s')", user.getName(), user.getAge(), user.getAccountBalance(), user.getLocation());
            statement.execute(insertInfo);
            String log = String.format("User with name %s is posted successfully\n", user.getName());

            LogActivity.writeFile(log);
            return String.format("User with name %s is posted successfully \n", user.getName());

        } catch (SQLException e) {
            String log = String.format("User with name %s is not successfully posted \n", user.getName());
            LogActivity.writeFile(log);
            return String.format("User with name %s is not successfully posted \n", user.getName());
        }
    }

}

class Users{
    private String name;
    private int age;
    private long accountBalance;
    private String location;

    public Users(String name, int age, String accountBalance, String location){
        this.name = name;
        this.age = age;
        this.accountBalance = Long.parseLong(accountBalance);
        this.location = location;
    }
    public Users(){

    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public int getAge(){
        return age;
    }
    public  void setAge(int age){
        this.age = age;
    }
    public long getAccountBalance(){
        return accountBalance;
    }
    public void setAccountBalance(long accountBalance){
        this.accountBalance = accountBalance;
    }
    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location = location;
    }
}
