import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Formatter;
import java.util.HashMap;
// import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdbc.db;

public class login {

    public static Scanner sc=new Scanner(System.in);
    //non ws char - \S
    public static boolean passChecker(String pass){
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^~!<>&+=])(?=\\S+$).{5,10}$";
        Pattern pattern=Pattern.compile(regex);
        if(pass==null){
            return false;
        }
        Matcher matcher=pattern.matcher(pass);
        return matcher.matches();        
    }


    public static void signup() throws Exception{
        System.out.println("\n---- Sign Up Menu ----\n");
        System.out.println("Enter Username: ");

        String user=sc.nextLine();
        String salt="",mySecurePassword="";

        //check if user already exists;
        db.getInstance();

        ResultSet getUsers=db.query.executeQuery("select id from users where username=\""+user+"\"");
        if(getUsers.next()!=true){

            System.out.println("Username available!\nEnter Password: ");
            
            String password=sc.nextLine();
            while(passChecker(password)!=true){
                System.out.println("Re-enter Password: (STRONG PASSWORD REQUIRED)");
                password=sc.nextLine();

                //set salt length = 30
                salt = encryptPassword.getSalt(30);
                mySecurePassword = encryptPassword.generateSecurePassword(password, salt);
            }

            db.insert("insert into users(username,password,salt) values(\"" + user + "\",\"" + mySecurePassword + "\",\"" + salt + "\")");
            System.out.println("Signed Up Successfully!!\n");


        }
        else{
            System.out.println("Username already taken!!\n");

        }


    }

    public static void viewStats() throws SQLException{
        System.out.print("\nEnter Host IP Address: ");
        String inputHost=sc.nextLine();
        String hostID="";
        ResultSet hostSet=db.query.executeQuery("select * from host");
                    
        // hashmap for retrieving host and ip from DB
        
        HashMap<String,String> dbHashMap=new HashMap<String,String>();        
        
        while(hostSet.next()){

            dbHashMap.put(hostSet.getString(1),hostSet.getString(3));

        }

        if(dbHashMap.containsValue(inputHost)){
            for (String o : dbHashMap.keySet()) 
                if (dbHashMap.get(o).equals(inputHost)) {
                  hostID=o;
                }

            ResultSet ipSet=db.query.executeQuery("select status, count(*) as count from pings where hostid="+hostID+" group by status");
            Formatter format=new Formatter();
            format.format("%s","\n--------------------------------------------------------\n");
            format.format("%20s %35s\n", "status","count");
            format.format("%s","--------------------------------------------------------\n");

            while(ipSet.next()){
                format.format("%30s %23s\n", ipSet.getString(1),ipSet.getString(2));
            }
            format.format("%s","\n--------------------------------------------------------\n");
            System.out.println(format);
            format.close();

        }
        else{
            System.out.println("IP Address doesn't exist!!\n");
        }


    }
    
    public static void searchHost() throws SQLException{
        System.out.print("Enter pattern: ");
        String ipPattern=sc.nextLine();  
        int found=0;
        {
        //method 1 -- retrive using java regex
        // ResultSet hostSet=db.query.executeQuery("select ip from host");

        // HashSet<String> ips=new HashSet<String>();
        // while(hostSet.next()){
        //     ips.add(hostSet.getString(1));
        // }

        // String regex="("+ipPattern+")"+"(.*)";
        // Pattern pattern=Pattern.compile(regex);
        // int found=0;
        // System.out.println("Possible matches:\n");
        // for(String ip:ips){
        //     Matcher matcher=pattern.matcher(ip);
        //     if(matcher.matches()){
        //         found=1;
        //         System.out.println(ip);
        //     }

        // }

        // if(found==0){
        //     System.out.println("No match found!!\n");
        // }
        }

        //method -2
        // if(Character.isAlphabetic(ipPattern.charAt(ipPattern.length()-1))){
            ResultSet hostSet=db.query.executeQuery("select name from host where name like binary"+" '%"+ipPattern+"%'");
            System.out.print("Possible Matches: ");
            while(hostSet.next()){
                found=1;
                System.out.println(hostSet.getString(1));
            }

            // if(found==0){
            //     System.out.println("None");
            // }

        // }

        // else{
            
            hostSet=db.query.executeQuery("select ip from host where ip like"+" '%"+ipPattern+"%'");
            // System.out.print("Possible Matches: ");
            while(hostSet.next()){
                found=1;
                System.out.println(hostSet.getString(1));
            }
            if(found==0){
                System.out.println("None");
            }

        // }

    }
    public static void loginUser(String user, String password) throws SQLException, IOException, InterruptedException {

        System.out.println("*** User Login Menu ***");

        System.out.print("Username: ");
        user=sc.nextLine();

        ResultSet getUsers=db.query.executeQuery("select id from users where username=\""+user+"\"");
        if(getUsers.next()!=true){
            System.out.println("Username unavailable!!");
            return;
        }

        else{

            System.out.print("Password: ");
            password=sc.nextLine();

            ResultSet getUserPass=db.query.executeQuery("select salt,password from users where username=\""+user+"\"");
            if(getUserPass.next()==true){
                if(encryptPassword.verifyUserPassword(password, getUserPass.getString(2), getUserPass.getString(1))==false){
                    System.out.println("Incorrect Password!!");
                    return;
                }
            

                else{

                            String choice="4";
                            System.out.println("\n\n--- Welcome "+ user +"! ---\n");
                            do{

                                System.out.print("\nMAIN MENU\n\n1.Inititate Pings\n2.View Stats\n3.Search Hostname by Pattern\n4.Logout\n5.Stop Pings\n\nEnter Choice: ");
                                choice=sc.nextLine();
                                switch (choice) {
                                    case "1":
                                        if(mainThread.running){
                                            System.out.println("Ping already initiated!!");
                                            break;
                                        }
                                        System.out.println("\nInititating pinging...");
                                        // Runtime rt = Runtime. getRuntime();
                    
                                        // rt. exec(new String[]{"cmd.exe","/c","start runas /user:Administrator","cmd"});
                                        mainThread.threadExec();
                                        mainThread.stop();                            
                                        break;
                                    case "2":     
                                        System.out.println();                                   
                                        viewStats();
                                        break;
                                    case "3":
                                        System.out.println();
                                        searchHost();
                                        break;
                                    case "4":
                                        return;
                                    case "5":
                                        if(!mainThread.running){
                                            System.out.println("Ping not inititated!!");
                                            break;
                                        }
                                        mainThread.exit=true;
                                        mainThread.stop();
                                        System.out.println("Ping stopped!");
                                        break;
                                    //     db.close();
                                    //     // System.exit(0);
                                
                                    default:
                                        System.out.println("Invalid Choice!!");                    
                                        break;
                                }
                    
                    
                            }while(choice!="4");
                    
                            
                    


                }
            }


        }        

    }


    public static void main(String[] args) throws Exception{
        String choice="";

        //opening db in main
        
        db.getInstance();


        do{

            System.out.print("\nUSER MENU\n\n1.Sign Up\n2.Login\n3.Exit\n\nEnter Choice: ");
            choice=sc.nextLine();
            switch (choice) {
                case "1":
                    signup();                    
                    break;
                case "2":
                    System.out.println();
                    loginUser("user", "password");
                    break;
                case "3":
                    db.close();
                    System.exit(0);
            
                default:
                    System.out.println("Invalid Choice!!");                    
                    break;
            }


        }while(choice!="3");



    }
    
}
