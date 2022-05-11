import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVWriter;


public class ping {    
    //validate ip address using regex
    static boolean ipchecker(String s){
        String chars="(\\d{1,2}|(0|1)\\"+"d{2}|2[0-4]\\d|25[0-5])";
        String ip=chars+"\\."+chars+"\\."+chars+"\\."+chars;
        Pattern p=Pattern.compile(ip);
        if(s==null)
            return false;
        Matcher m=p.matcher(s);
        return m.matches();        
    }

    static void pingcmd(String c){
        try {
            Process p=Runtime.getRuntime().exec(c);
            Date date = new Date();
            Timestamp timestamp2 = new Timestamp(date.getTime());
            BufferedReader b=new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader e=new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s,h,t="",pk="",ti=timestamp2.toString(); //timestamp
            //adding status
            String st="";
            CSVWriter csv = new CSVWriter(new FileWriter("C:/Users/HP/Documents/Zoho/a.csv",true));
            
            String[] tokens = c.split(" ");
            String delims = "[ ]+";
            h=tokens[tokens.length - 1]; //hostname
            Integer i=0;
            // System.out.println("Ping "+ ++i +": ");
            while((s=b.readLine()) != null){

              
                    if(s.indexOf("Average") != -1)
                {
                    tokens = s.split(delims);
                    t=tokens[tokens.length - 1]; //time
                    System.out.println(t+"]");
                    st="ping successful";

                    
                }

                if(s.indexOf("Lost =")!=-1){
                    tokens = s.split(" ");
                    pk=tokens[tokens.length - 2]; //pk loss
                    tokens=pk.split("\\(");
                    pk=tokens[1];
                    System.out.println(pk+"}");
                }

                
                
                         
                // if(s.startsWith("Pinging")!=true && !s.equals("")){
                //     if(i==1||i==0){ //to track the line number --- 1st or 2nd line incase of error
                //         //st=s;
                //         if(s.contains("Please")){
                //             st=s;
                //             String[] err=st.split(" P");
                //             st=err[0];
                //             System.out.println(st);
                //         }
                //         else{
                //             st=s;
                //             System.out.println(st);
                //         }
                //         ++i;
                //         //break;
                //     }                   
                   
                    
                // }

                if(s.startsWith("Reply")!=true && !s.equals("") && i<2){
                    //st=s;
                    
                    if(s.contains("Please")){
                        st=s;
                        String[] err=st.split(" P");
                        st=err[0];
                        System.out.println(st+"?");
                        break;
                    }                    
                    
                }
                
                if(s.startsWith("Reply")){
                    tokens=s.split(": ");
                    if(!(tokens[1].startsWith("bytes"))){
                        st=tokens[1];
                        System.out.println(st+"*");
                        //break;
                    }
                }
                else if(i<=2 && !s.equals("") && !s.startsWith("Pinging")){
                    st=s;

                }
                
                
                ++i;
            }
            while((s=e.readLine())!=null){
                //System.out.println("booo");
                System.out.println(s);
            }
            String[] data={h,t,pk,ti,st};
            csv.writeNext(data);
            b.close();
            e.close();
            csv.close();
            
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
            //System.out.println(e);
        }
        



    }
    public static void main(String[] args) throws Exception {
        //timer task to schedule process
        // Scanner sc=new Scanner(System.in);
        // String str="";
        // if(sc.hasNextLine())
        //     str=sc.nextLine();
        // sc.close();      

        TimerTask t=new TimerTask() {
            @Override
            public void run(){
                //take user input for hostname as cli
                try{
                    String cmd="ping -n 1 " + args[0];
                    pingcmd(cmd);  
                }
                catch(Exception e){
                    e.printStackTrace();
                }

            }
        };
        // CSVWriter csv = new CSVWriter(new FileWriter("C:/Users/HP/Documents/Zoho/a.csv"));
        //     //headers
        // String[] entries = {"Hostname","TimeTaken","PacketLoss","Timestamp","Connectivity Status"};
        // csv.writeNext(entries);//           ---> headers already included 
        // csv.close();
        Timer ti=new Timer();
        long delay=0;
        long period=15000; //ms
        ti.scheduleAtFixedRate(t, delay, period);
    }
    
}
// java ping > a.txt
//ping response after 15 secs

// The loopback address aka localhost -> an internal address that routes back to the local system. 
//IPv4 ~ 127.0.0.1    IPv6 ~ 0:0:0:0:0:0:0:1 or ::1.

//TTL used in packets forwarding from router to router ~ indicates lifetimes of the packet 
//or the # of routers it can pass through till it reaches destination beyond which expires
//TTL=0 means packet will not be forwarded further

//time ~ packet sent from device to server and back to device
// why ttl not displayed for ipv6? 
//127.0.0.1 (also a loopback addr) is treated as an IP address hence it contacts DNS server and gives TTL in o/p
//localhost is pinging the same system hence TTL is not required

//add new status for ping successful and the other fields with null values