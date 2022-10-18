package pingCmd;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
// import java.util.NoSuchElementException;
// import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdbc.db;

public class ping extends Thread{

    //to resolve host and ip naming

    public static String input="";

    
    static String getHostIP(String host){

        try{

            InetAddress address=InetAddress.getByName(host);
            System.out.println(address.getHostAddress()+"/");
            return address.getHostAddress();

            }
            catch(UnknownHostException e){

                e.printStackTrace();
                return "Error";

            }

    }
        
    //validate ip address using regex

    static boolean ipChecker(String ipAddress){
    
        String chars="(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";
        String ip=chars+"\\."+chars+"\\."+chars+"\\."+chars;
        Pattern pattern=Pattern.compile(ip);
        if(ipAddress==null)
            return false;
        Matcher matcher=pattern.matcher(ipAddress);
        return matcher.matches();        
    
    }

    //retrieve ping stats and update DB

    public static void pingcmd(String command){

        try {
        
            //execute command

            Process p=Runtime.getRuntime().exec(new String[]{command});
            Date date = new Date();
            Timestamp timeStamp = new Timestamp(date.getTime());
            
            //read ping output
            
            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader errors=new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String buffer,host,responseTime="",packetLoss="",time=timeStamp.toString();
            String status="";

            String[] tokens = command.split(" ");
            String delims = "[ ]+";
            
            host=tokens[tokens.length - 1];
            Integer line=0;

            while((buffer=reader.readLine()) != null){

                    //obtain response time

                    if(buffer.indexOf("Average") != -1){

                        tokens = buffer.split(delims);
                        responseTime=tokens[tokens.length - 1]; //time
                        //System.out.println(t+"]");
                        status="ping successful";

                    }

                    //obtain packet loss

                    if(buffer.indexOf("Lost =")!=-1){

                        tokens = buffer.split(" ");
                        packetLoss=tokens[tokens.length - 2];
                        tokens=packetLoss.split("\\(");
                        packetLoss=tokens[1];

                    }             

                    //trim unnecessary lines from error messages

                    if(buffer.startsWith("Reply")!=true && !buffer.equals("") && line<2){

                        if(buffer.contains("Please")){

                            status=buffer;
                            String[] error=status.split(" P");
                            status=error[0];
                            break;

                        }                                       

                    }

                    //obtain status message for pinging IP
                
                    if(buffer.startsWith("Reply")){
                        
                        tokens=buffer.split(": ");
                        if(!(tokens[1].startsWith("bytes"))){

                            status=tokens[1];

                        }

                    }

                    //obtain status message for pinging hostname

                    else if(line<=2 && !buffer.equals("") && !buffer.startsWith("Pinging")){

                        status=buffer;

                    }                
                
                    ++line;

            }

            //buffer prints error stream

            while((buffer=errors.readLine())!=null){

                System.out.println(buffer);

            }
           
            Integer fetchHostID=0;

            ResultSet getHostIdSet=db.query.executeQuery("select id from host where name=" + "\"" + host + "\" or ip=" + "\"" + host + "\"");
            
            while(getHostIdSet.next()){

                fetchHostID=Integer.parseInt(getHostIdSet.getString(1));

            }

            //error handler for fetch failure

            if(fetchHostID==0){ 
            
                System.out.println("Error!!"); 
                System.exit(0);
            
            }

            db.insert("insert into pings(hostid,ResponseTime,loss,time,status) values(\"" + fetchHostID + "\",\"" + responseTime + "\",\"" + packetLoss + "\",\"" + time + "\",\"" + status + "\")");


            reader.close();
            errors.close();
            
        } catch (Exception e) {

            // e.printStackTrace();

        }  

    }

    public void run(){

        // timer to schedule ping

        TimerTask task=new TimerTask() {
            @Override
            public void run(){

                try{

                    String command="ping -n 1 " + input;                                       
                    ResultSet hostSet=db.query.executeQuery("select * from host");
                    
                    // hashmap for retrieving host and ip from DB
                    
                    HashMap<String,String> dbHashMap=new HashMap<String,String>();        
                    
                    while(hostSet.next()){

                        dbHashMap.put(hostSet.getString(2),hostSet.getString(3));

                    }

                    //IP address validation for NULL valued hostnames ---> DNS unresolved

                    if(ipChecker(input)){

                        //check if IP exists

                        if(dbHashMap.containsValue(input)){}

                        else{

                            //NULL values for unresolved hosts

                            if((InetAddress.getByName(input).getHostName()).equals(input)){

                                db.insert("insert into host(name,ip) values(\"NULL" + "\",\"" + input + "\")");

                            }
                            
                            else{

                                //insert resolved values 

                                db.insert("insert into host(name,ip) values(\"" + InetAddress.getByName(input).getHostName() + "\",\"" + input + "\")");

                            }
                        
                        }

                    }
                    
                    else{

                        //check if hostname exists

                        if(dbHashMap.containsKey(input)){

                            //update IP address if hostname exists

                            String newHostIP=getHostIP(input);

                            if(newHostIP.equals(dbHashMap.get(input))){}

                            else{

                                db.insert("update host set ip=\"" + newHostIP + "\" where name=\"" + input + "\"");
                            
                            }

                        }

                        else{

                            db.insert("insert into host(name,ip) values(\"" + input + "\",\"" + getHostIP(input) + "\")");
                            
                        }
                    }

                    // connection.close();
                    pingcmd(command);

                }

                catch(Exception e){

                    e.printStackTrace();

                }

            }
        };
        
        //timer to run task every 15 sec

        Timer timer=new Timer();
        long period=15000; //ms
        timer.scheduleAtFixedRate(task, 0, period);

    }

    
    static void pingFunc(){
        db.getInstance();

        // //read user input for ping

        // Scanner sc=new Scanner(System.in);
        // if(sc.hasNextLine())
        //     input=sc.nextLine();
        
        // // read # of threads
        
        // input=sc.nextLine();




        ping instance=new ping();
        instance.start();
        
        System.out.println("Started thread");

        //waits for interruptions
        // try {

        //     sc.next();
            
        // } catch (NoSuchElementException e) {

        // }
        
        System.out.println("closing connections");
        // sc.close();
        db.close();

    }
    public static void main(String[] args) throws Exception {
        
        

    }
    
}

/*
    The loopback address aka localhost -> an internal address that routes back to the local system. 
    IPv4 ~ 127.0.0.1    IPv6 ~ 0:0:0:0:0:0:0:1 or ::1.

    TTL used in packets forwarding from router to router ~ indicates lifetimes of the packet 
    or the # of routers it can pass through till it reaches destination beyond which expires
    TTL=0 means packet will not be forwarded further

    time ~ packet sent from device to server and back to device
    why ttl not displayed for ipv6? 
    127.0.0.1 (also a loopback addr) is treated as an IP address hence it contacts DNS server and gives TTL in o/p
    localhost is pinging the same system hence TTL is not required

    add new status for ping successful and the other fields with null values
*/