import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jdbc.db;

import pingCmd.ping;

public class mainThread{


    static ExecutorService executor;
    static ping pingObj;
    static Integer i=1;
    static MyRunnable worker;
    static HashSet<String> ipHashSet=new HashSet<String>();
    static Timer timer;
    public static boolean exit=false;
    public static boolean running=false;

    public static Scanner sc=new Scanner(System.in);


    public static boolean isAdministrator() throws IOException{
        
        //to identify admin
        // Runtime rt=Runtime.getRuntime();
        //Process p=rt.exec("start");

        // Runtime.getRuntime().exec(new String[]{"cmd", "/k", "start", "runas","/user:administrator","regedit"});

        Process p=Runtime.getRuntime().exec(new String[]{"net session"});

        
        BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
        String buffer=reader.readLine();
        if(buffer==null)
            //System.out.println("hi");
            return false;
        else
            return true;

            // rt.exec("powershell start-Process cmd -verb runas");
            /*"ping -n 1 8.8.8.8");
            // p=Runtime.getRuntime().exec("cmd.exe");  //using cmd child
            // p=Runtime.getRuntime().exec("runas /profile /user:Administrator /savecred");
            



        }*/
        

        //p.waitFor();             

}

    public static void threadExec() throws IOException, InterruptedException{
        if(isAdministrator()==false){

            // Runtime.getRuntime().exec("powershell \"start cmd \\\"/k \"cd \"C:\\Users\\HP\\Documents\\Zoho\\ping\\ping_cmd\" & javac -classpath mysql.jar ping.java db.java mainThread.java & java -cp .;\"mysql.jar\" mainThread\"\"\" -v runAs"); //,"cmd", "/k", "start"});
            // Runtime.getRuntime().exec("runas /user:Administrator cmd"); // \\\"/k \"C:\\Users\\HP\\Documents\\Zoho\\ping\\ping_cmd\"");
            // Process p=Runtime.getRuntime().exec(new String[]{"cmd.exe","/c","runas /user:","Administrator","start","cmd"}); //, "\"cmd.exe\""}); // \"start cmd /k cd C:\\Users\\HP\\Documents\\Zoho\\ping\\ping_cmd & java -cp .;\"mysql.jar\" mainThread\"");
            // p.waitFor();
            Runtime rt = Runtime. getRuntime();

            rt. exec(new String[]{"cmd.exe","/c","start runas /user:Administrator","cmd"});

            System.out.println("Error\n Access Denied!!");
            System.exit(0);
            
        }
        //System.exit(0);



        //create fixed no of threads

        System.out.println("Enter number of threads: ");


        final Integer noOfThreads=sc.nextInt();

        executor=Executors.newFixedThreadPool(noOfThreads);

        db.getInstance();

        //write safe ip input

        BufferedReader bufferedReader=new BufferedReader(new FileReader("C:/Users/HP/Documents/Zoho/ping/ping_jdbc/ip-input.txt"));
        String ipList;

        while((ipList=bufferedReader.readLine())!=null){
            ipHashSet.add(ipList);
            }
        
        bufferedReader.close();

        

        // mainThread instance=new mainThread();
        // instance.start();


        // executor.shutdown();
        // while (!executor.isTerminated()) {
		//     // empty body
		// }

        //waits for interruptions
       

            
        
        // System.out.println("closing connections");
        // sc.close();
        // db.close();
        

        // run indefinitely
		// System.out.println("\nFinished all threads");

    
  


        //time for 20sec interval

        TimerTask task=new TimerTask() {
            @Override
            public void run(){

                try{
                    
                    // System.out.println("Set "+ i++);

                    ResultSet hostSet=db.query.executeQuery("select * from host");
                    HashSet<String> dbHashSet=new HashSet<String>();        
            
                    //retrieve only IPs from DB
                    while(hostSet.next()){
            
                        dbHashSet.add(hostSet.getString(3));
            
                    }
            
                    //iterate csv

                    // //write safe ip input
            
                    // BufferedReader bufferedReader=new BufferedReader(new FileReader("C:/Users/HP/Documents/Zoho/ping/ping_jdbc/ip-input.txt"));
                    // String ipList;

                    // HashSet<String> ipHashSet=new HashSet<String>();
                    // while((ipList=bufferedReader.readLine())!=null){
                    //     ipHashSet.add(ipList);
                    //     }

                    for(String m:ipHashSet){
                        // System.out.println(m);
                    
                        if(dbHashSet.contains(m)){
                            // worker=new MyRunnable(m);
                            // executor.execute(worker);    
            
                        }
                        else{

                            if((InetAddress.getByName(m).getHostName()).equals(m)){

                                db.insert("insert into host(name,ip) values(NULL" + ",\"" + m + "\")");

                            }

                            else{

                                db.insert("insert into host(name,ip) values(\"" + InetAddress.getByName(m).getHostName() + "\",\"" + m + "\")");

                            }
            
                        }
                        
                        worker=new MyRunnable(m);
                        executor.execute(worker);                   
                        
                    }                  
        
            }
            catch(InterruptedException e){
                // e.printStackTrace();
                // System.out.println("dvbg");
                // Future<?> future=executor.submit(worker);
                // future.cancel(true);

            } catch (SQLException e) {
            } catch (UnknownHostException e) {
            } catch (Exception e) {
            }
        

            }
        };

        timer=new Timer();
        long period=5000; //ms
        mainThread.running=true;
        timer.scheduleAtFixedRate(task, 0, period);

    }

    public static void stop(){
        if(!exit){
            return;
        }
        else{
            timer.cancel();
            exit=false;
            running=false;
        }

    }


    public static void main(String[] args) throws Exception {

        threadExec();
        




        
    }
        
    
    
    public static class MyRunnable implements Runnable{
        private String ip;
        MyRunnable(String ip){
            this.ip=ip;


        }
        
        @Override
        public void run() {

            
            // long threadId = Thread.currentThread().getId();
            // System.out.println(threadId);

            try{
                ping.input=ip;
                ping.pingcmd("ping -n 1 " + ip);
                // Thread.sleep(5000);
                
            }
            catch(Exception e){
                // e.printStackTrace();
                // Thread.currentThread().interrupt();
            }

            
            
            
        }
    }
}
