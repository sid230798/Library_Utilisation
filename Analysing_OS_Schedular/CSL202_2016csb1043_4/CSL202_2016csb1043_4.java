/*
Author : Siddharth Nahar
Entry No. : 2016csb1043
Use : Extract info from top command
      and Scheduling tasks and mailing System
Date : 22/3/18
*/


/*Packages import for running Scheduler Tasks and ProcessBuilder
  Another Class is made for Mail Sending*/
import java.lang.*;
import java.util.*;
import java.io.*;
import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


/*This will stroe Info*/
 
class Violaters{

        
        /*To store Info about each Process which violates the fields
          Proc_Id = PrcessID,CPU,Mem %Usage,Time for which it has been 
          running and Command and User name*/
        String Proc_ID;
        double _CPU_Usage,_MEM_Usage;
        String Time;
        String User,Command;
               
        /*Constructor to initialise Violaters members*/
        public Violaters(String pId,String u,double cpu,double mem,String t,String command){
        
                Proc_ID = pId;
                User    = u;
                _CPU_Usage = cpu;
                _MEM_Usage = mem;
                Time = t;
                Command    = command;
                
        }
        
        public Violaters(){
        
        }
        
        public int SendEmails(List<Violaters> list,String[] Recipients,double cpu,double mem,double cpu_t,double mem_t){
        
               if(list.size()>0){
               
                        System.out.println("Process were Found to Send Emails : ");
                        System.out.println("Prcess Info is : ");                
                        final String username = "blood11connect@gmail.com";
		        final String password = "chomuismyname";

		        Properties props = new Properties();
		        props.put("mail.smtp.auth", "true");
		        props.put("mail.smtp.starttls.enable", "true");
		        props.put("mail.smtp.host", "smtp.gmail.com");
		        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		
		        props.put("mail.smtp.port", "587");
        
		        Session session = Session.getInstance(props,
		          new javax.mail.Authenticator() {
			        protected PasswordAuthentication getPasswordAuthentication() {
				        return new PasswordAuthentication(username, password);
			        }
		          });
		          
		        String EmailData = "Max. CPU usage Allowed : %" + cpu + "  For Duration : " + cpu_t + "Seconds\n";
		        EmailData += "Max. Mem usage Allowed : %" + mem + "  For Duration : "+mem_t+"Seconds\n";
		        for(Violaters obj : list){
		        
		            EmailData += "\nPID : "+obj.Proc_ID+"\nUserName : "+obj.User+"\n%CPU usage : "+obj._CPU_Usage+"\n%MEM usage : "+obj._MEM_Usage;
		            EmailData +=  "\nTime : "+obj.Time+"\nCommand Name : "+obj.Command;
		            
		            EmailData += "\n--------------------------------------------------------------";
		        
		        
		        }
                        System.out.println(EmailData);
		        try {

                                 Message message = new MimeMessage(session);
                                
                                for(int i=0;i<Recipients.length;i++){
                                
                                        
			                message.addRecipient(Message.RecipientType.TO, new InternetAddress(Recipients[i]));
			        
			        }         
			        //message.addRecipient(Message.RecipientType.TO, new InternetAddress(Recipients[0]));
			        //message.addRecipient(Message.RecipientType.TO, new InternetAddress("sid23nahar@gmail.com"));
			        message.setFrom(new InternetAddress("Siddharth"));
			        message.setSubject("Testing Subject");
                                message.setText("You Have Violated This Process : \n"+EmailData);

			        Transport.send(message);
			        

			        System.out.println("Done");
			        return 1;

		        } catch (MessagingException e) {
			        System.out.println(e.getMessage());
			        return -1;
		        }
               
               
               
               }
        
        
                return 0;
        
        
        
        }
        
}


class RunSchedulerTask{

        /*SchedulerExecuterService object to run the Scheduled
          Command for infinite time at every 4 seconds*/    
        private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        static double  TimeOut,CPULimit,CPUTime,MemLimit,MemTime;
        static String Address[];
        static Map<String,Integer> ViolateTime;
        static Map<String,Integer> EmailViolate;
        static long timedone=0;
        //Violater Global;
        /*RunTop function Which runs top command every 4 seconds
          and Gets violating Condition and Send emails*/
        public int RunTop() {
   
                       
               // ViolateTime = new HashMap<String,Integer>();
                /*Runnable class which runs the command*/
                final Runnable beeper = new Runnable() {
                
                        public void run() { 
                                
                                
                                List<Violaters> DataEmails = new ArrayList<Violaters>();
                                try{
                                        FileWriter out = new FileWriter("Previous.txt");
                                        /*Command to run top in batch mode */
                                        List<String> commands = new ArrayList<String>();
                                        commands.add("top");
                                        commands.add("-b");
                                        commands.add("n1");
                                        commands.add("-o");
                                        commands.add("-COMMAND");
                                        
                                        /*Start ProcessBuilder Command to run top command every 4 seconds*/
                                        
                                        ProcessBuilder Top = new ProcessBuilder(commands);
                                        Process process    = Top.start();
                                        
                                        /*Read Output of command from InputStream of process*/
                                        BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    
                                            
                                        /*Using sort by comand enables to get multiple instances in one go*/
                                        String key=null;
                                        //double PrevCpu=0,PrevMem=0;
                                        double cpu=0,mem=0;
                                        String line = null;
                                        //String Prev = null;
                                        String prev = null;
                                        int count=0,check,Prevcheck=0;
                                        /*Read each line of the output of top command*/
                                        while((line = stdInput.readLine()) != null){
                                                     
                                                  
                                                             
                                                count++;
                                                /*Leave the first 8 lines of output*/
                                                if(count < 8)
                                                        continue;
                                                        
                                                
                                                else{
                                                
                                                        /*Split the line by whitespace*/
                                                        check = 0;
                                                        String part[] = line.split("\\s+");
                                                        
                                                        String newkey = null;
                                                        
                                                        String uu=null;
                                                        
                                                        if(part[0].equals("")){
                                                                newkey   = part[2]+part[12];
                                                                //cpu  = Double.parseDouble(part[9]);
                                                                uu   = part[12];
                                                                check = 1;   
                                                                //mem = Double.parseDouble(part[10]);
                                                        }        //newKey = part[2]+part[12];
                                                        else{
                                                                newkey   = part[1]+part[11];
                                                                //cpu = Double.parseDouble(part[8]);
                                                                uu = part[11];        
                                                                //mem = Double.parseDouble(part[9]);
                                                                //newKey = part[1]+part[11];
                                                        }
                                                        
                                                        if(newkey.equals(key)){
                                                        
                                                                cpu += Double.parseDouble(part[8+check]);
                                                                mem += Double.parseDouble(part[9+check]);
                                                                
                                                        
                                                        }else{
                                                        
                                                                
                                                                /*Check for Memory and Cpu limt and insert into Hashtable*/
                                                                if((cpu > CPULimit || mem > MemLimit)){
                                                                
                                                                        //System.out.println(key +" "+cpu);
                                                                        if(ViolateTime.containsKey(key)){
                                                                        
                                                                                //
                                                                                ViolateTime.put(key,ViolateTime.get(key)+2);
                                                                                //System.out.println(ViolateTime.get(key));
                                                                                if((ViolateTime.get(key) > CPUTime || ViolateTime.get(key) > MemTime) && (EmailViolate.containsKey(key) == false)){
                                                                                        
                                                                                                String Prevpart[] = prev.split("\\s+");
                                                                                                String pid = (Prevpart[0+Prevcheck]);
                                                                                                String User = Prevpart[1+Prevcheck];
                                                                                                double _cpu = Double.parseDouble(Prevpart[8+Prevcheck]);
                                                                                                double _mem = Double.parseDouble(Prevpart[9+Prevcheck]);
                                                                                                String time = Prevpart[10+Prevcheck];
                                                                                                String cmd = Prevpart[11+Prevcheck];
                                                                                                Violaters obj = new Violaters(pid,User,_cpu,_mem,time,cmd);
                                                                                                DataEmails.add(obj);
                                                                                                System.out.println(key+" "+User+" "+cmd);
                                                                                        
                                                                                        
                                                                                        EmailViolate.put(key,1);                                        
                                                                                        
                                                                                }
                                                                                
                                                                                 
                                                                                 System.out.println(key+" "+cpu+" "+mem+" "+ViolateTime.get(key));
                                                                        
                                                                        }
                                                                        else{
                                                                                 
                                                                                ViolateTime.put(key,0);
                                                                                
                                                                        }                   
                                                                
                                                                }
                                                                
                                                                /*Replacing values of key cpu and mem*/
                                                                key = newkey;
                                                                cpu = Double.parseDouble(part[8+check]);
                                                                mem = Double.parseDouble(part[9+check]);
                                                                //Prevpart = part;
                                                                prev = line;
                                                                Prevcheck = check;
                                                        
                                                      }        
                                                
                                                }
                                                  
                                        
                                     }
                                     /*Check the last key from output*/                                     
                                     if((cpu > CPULimit || mem > MemLimit)){
                                                                
                                                                        
                                        if(ViolateTime.containsKey(key)){
                                                                        
                                                                                //
                                                ViolateTime.put(key,ViolateTime.get(key)+2);
                                                                                //System.out.println(ViolateTime.get(key));
                                                if((ViolateTime.get(key) > CPUTime || ViolateTime.get(key) > MemTime) && (EmailViolate.containsKey(key) == false)){
                                                                                        
                                                        String Prevpart[] = prev.split("\\s+");                               
                                                        String pid = (Prevpart[0+Prevcheck]);
                                                        String User = Prevpart[1+Prevcheck];
                                                        double _cpu = Double.parseDouble(Prevpart[8+Prevcheck]);
                                                        double _mem = Double.parseDouble(Prevpart[9+Prevcheck]);
                                                        String time = Prevpart[10+Prevcheck];
                                                        String cmd = Prevpart[11+Prevcheck];
                                                        Violaters obj = new Violaters(pid,User,_cpu,_mem,time,cmd);
                                                        DataEmails.add(obj);
                                                        //System.out.println(key+" "+User+" "+cmd);
                                                                                       
                                                                                        
                                                        EmailViolate.put(key,1);                                        
                                                                                        
                                                 }
                                                                                
                                                                                 
                                                 System.out.println(key+" "+cpu+" "+mem+" "+ViolateTime.get(key));
                                                                        
                                         }
                                         else{
                                                                                 
                                                 ViolateTime.put(key,0);
                                                                                
                                         }                   
                                                                
                                     }
                                     timedone += 2;
                                     System.out.println("--------------------------------"); 
                                       
                                       Violaters obj = new Violaters();
                                       int k = obj.SendEmails(DataEmails,Address,CPULimit,MemLimit,CPUTime,MemTime);
                                       if(k == -1){
                                                
                                                for(Violaters check_ : DataEmails){
                                                
                                                        EmailViolate.remove(check_.Proc_ID);
                                                
                                                
                                                }
                                       
                                       }
                                       
                                       out.write("TimeOut = "+timedone+"\n");
                                       for(Map.Entry<String,Integer> entry : ViolateTime.entrySet()){
                                       
                                                out.write(entry.getKey() + " = " + entry.getValue()+"\n");
                                                
                                       
                                       }
                                       
                                       out.write("Email = ");
                                       for(Map.Entry<String,Integer> entry : EmailViolate.entrySet()){     
                                     
                                                out.write(entry.getKey()+" ");
                                        }
                                        
                                        out.close();
                                        
                                }catch(Exception e){
                                
                                       System.out.println(e.getMessage());
                                }
                        
                        
                                         
                       }
                       
                       
                };
     
                /*Scheduling above process for every 2sec*/
                final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 2, SECONDS);
                /*BeeperStop used To Shutdown Thread after given Interval*/
                Runnable beeperStop = new Runnable() {
                        public void run() { 
                                beeperHandle.cancel(true);
                                scheduler.shutdown(); 
                        }
                };
       
                scheduler.schedule(beeperStop, (long)(TimeOut*60)-timedone, SECONDS);
                
                
                return 1;
             // scheduler.shutdown(); 
                        
        }
        
        public static void main(String [] args)throws Exception{
        
        
                
                
                
		//System.out.println(Address[0]+"t\n"+Address[1]+"w");
	       
	       File PrevData = new File("Previous.txt");
	       ViolateTime = new HashMap<String,Integer>();
               EmailViolate = new HashMap<String,Integer>();
               int tp =0; 
                
                if(PrevData.exists()){
                
                        tp = 1; 
                         //System.out.println("Yes");
                        FileReader fw = new FileReader("Previous.txt");
                        BufferedReader b = new BufferedReader(fw);
                        String s = null;
                        while((s = b.readLine()) != null){
                        
                                int index = s.indexOf("=");
                                if(index == -1)
                                  continue;
                                else{
                        
                                    String initial = s.substring(0,index-1);
                                    String value   = s.substring(index+2);
                                    //System.out.println(initial+" "+value);
                                    if(initial.equals("TimeOut")){
                                    
                                        timedone = Long.parseLong(value);
                                        //System.out.println(timedone);
                                    
                                    }else if(initial.equals("Email")){
                                    
                                        String partition[] = value.split("\\s");
                                        for(String emails : partition){
                                        
                                           EmailViolate.put(emails,1);
                                           //System.out.println(emails);
                                        }
                                    
                                    
                                    }else{
                                    
                                        ViolateTime.put(initial,Integer.parseInt(value));
                                    
                                    }
                                }
                        
                        } 
                
                
                }else{
                
                        timedone=0;
                        //System.out.println("Not found");
                }
                
                      
                      while(true){
                       RunSchedulerTask obj = new RunSchedulerTask();
                
                
                
                        Properties Props = new Properties();
                        Scanner sc = new Scanner(System.in);
                        System.out.print("Please Input file Path for Settings Prpoerties file : ");
                        String path = sc.nextLine();
                        FileInputStream in = new FileInputStream(path);
                        Props.load(in);
                  
                        TimeOut  = Double.parseDouble(Props.getProperty("quota.window.minutes"));
                        CPULimit = Double.parseDouble(Props.getProperty("sustained.max.cpu.usage.limit"));
                        CPUTime  = 60*Double.parseDouble(Props.getProperty("sustained.max.cpu.usage.duration.limit"));
                        MemLimit = Double.parseDouble(Props.getProperty("sustained.max.memory.usage.limit"));
                        MemTime  = 60*Double.parseDouble(Props.getProperty("sustained.max.memory.usage.duration.limit"));
                        String Add = Props.getProperty("notify.emails");
                        
                       //System.out.println("Hello");
                       StringTokenizer st2 = new StringTokenizer(Add, " ,");
                       List<String> Addr = new ArrayList<String>();
                       
                        while (st2.hasMoreTokens()){
                                String s = st2.nextToken();
                                //System.out.println(s);
                                Addr.add(s);
                        }
			        //System.out.println(st2.nextToken());
			
                        Address = new String[Addr.size()];
                       for(int i=0;i<Addr.size();i++){
                       
                                Address[i] = Addr.get(i);
                       }
                        if(tp == 0){
                        
                                timedone=0;
                                ViolateTime = new HashMap<String,Integer>();
                                EmailViolate = new HashMap<String,Integer>();
                        }
                       int repeat =  obj.RunTop();
                       long tot = ((long)TimeOut*60 - timedone)*1000+1000;
                       Thread.sleep(tot);
                   }
                       /*
                       if(repeat == 1)
                          continue;
                       else
                          break; 
                         */  
                     
        }







}
