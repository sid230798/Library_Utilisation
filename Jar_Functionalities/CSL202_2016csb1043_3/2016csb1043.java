/*
 Author : Siddharth Nahar
 Entry Number : 2016csb1043
 Functionality : Code seeks to learn how to execute system commands and javap diassembler 
                 to explore classes in jar file.
 Date : 10/3/18
*/

import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/*This class is use as Comparator for Sorting maps by values*/
class CompareByValue<String> implements Comparator<String>
{
    Map cmap;
    public CompareByValue(Map cmap)
    {
        this.cmap = cmap;
    }
  /*To sort in descending Order by value*/
    public int compare(String a, String b)
    {
    	int A = (int)cmap.get(a);
    	int B = (int)cmap.get(b);
        if (A <= B)
            return 1;
        else
            return -1;
    }
}

/*This Class stores information About all 
 Classes storing its sizes,Number of methods,JVM instructions Map*/
class ClassesInformation{

      public String ClassName;
      public int sizeOfPool;
      public int CountMethods;
      public HashMap<String,Integer> JVMInstCount;
      
      /*Constructor allocates size to Hash Map*/
      public ClassesInformation(){
      
            JVMInstCount = new HashMap<String,Integer>();
      }
      
      /*Prints Information in format on Command Line*/
      public void PrintInfo(){
      
             
             System.out.println("ClassName = " + ClassName);
             System.out.println("Size of Constant Pool = " + sizeOfPool);
             System.out.println("Number of Methods in Given Class = " + CountMethods);
             if (JVMInstCount.isEmpty()){
                System.out.println("map is empty");
             }
             else{
                System.out.println(JVMInstCount);
             }
             
             System.out.println("-----------------------------------------------------------------------");
             System.out.println("-----------------------------------------------------------------------");
      
      }

}

/*Primitive Class for Working on System Commands and getting some info about classes*/
class Demo{


      /*To avoid reinitalization of ArrayList ,Returning new object of list*/
      public static List<String> getFreshObject(){
             
             return new ArrayList<String>();
      }
      
      /*Using Regular Expression to check Which is Function name and Constructor*/
      public static boolean checkFunctionName(String s){
      
            /*Regular Expression for functionName and params*/
            String fname = "\\s*[^( ]+\\s*[(][^)]*[)][^;]*;";
            /*Regular Expression Return Type */
            String returnType = "\\s*[^() ]+";
            /*Regular Expression fro non modular Access Specifier*/
	    String modifier = "\\s*(static|abstract|final)?";
	    /*Regular Expression for modular Access Specifier*/
            String AccessSpecifier = "\\s*(public|private|protected)?";
            
            if(Pattern.matches(AccessSpecifier+modifier+fname,s) == false && Pattern.matches(AccessSpecifier+modifier+returnType+fname,s) == true)
              return true;
            else
              return false;
      
      
      }
      
      
      
      public static ClassesInformation extractInfoComplete(List<String> Data){
      
           ClassesInformation obj = new ClassesInformation();
           String match = "^\\s*(Constant pool:)$";
           int i=0;
           while(Pattern.matches(match,Data.get(i++)) == false && i<Data.size()){
           }
           
           //System.out.println(i);
           /*Creating a map for Pool Table storing keywords and its corresponding sizes*/
           HashMap<String,Integer> PoolTable = new HashMap<String,Integer>();
           PoolTable.put("Class",3);
           PoolTable.put("Fieldref",5);
           PoolTable.put("Methodref",5);
           PoolTable.put("InterfaceMethodref",5);
           PoolTable.put("String",3);
           PoolTable.put("Integer",5);
           PoolTable.put("Float",5);
           PoolTable.put("Long",9);
           PoolTable.put("Double",9);
           PoolTable.put("NameAndType",5);
           PoolTable.put("Utf8",4);
           PoolTable.put("MethodHandle",4);
           PoolTable.put("MethodType",3);
           PoolTable.put("InvokeDynamic",5);  
           int PoolSize = 0;
           /*Logic to calculate pool size
             Extraction of constant pool keyword and matching it to above map
             and add all its size*/
           while(Pattern.matches("\\s*\\{",Data.get(i)) == false && i<Data.size()){
           
           
                String Pool = Data.get(i);
                int index  = Pool.indexOf("=");
                if(index == -1){
                  i++;
                  continue;
                }
                else{
                   i++;
                   Pool = Pool.substring(index+2);
                   index = Pool.indexOf(" ");
                   if(index == -1)
                     Pool = Pool.substring(0);
                   else
                     Pool = Pool.substring(0,index);
                   //Pool = Pool.substring(0,index);
                   if(PoolTable.containsKey(Pool)){
                      PoolSize++; //PoolTable.get(Pool);
                   }
                   else
                     System.out.println(Pool);
                   
                }
                
                
           }
           obj.sizeOfPool = PoolSize;
           //return PoolSize;
           
           /*Logic to get JVM instuctions and string it ina map with its frequency
            Traversing string and matching it with pattern observed*/
           HashMap<String,Integer> map = new  HashMap<String,Integer>();
           while(i<Data.size()){
           
               String fun = Data.get(i++);
              
               if(Pattern.matches("\\s*[0-9]+\\s*:\\s*[a-z].*",fun) == true){
                 
                 //System.out.println(fun);
                 /*Extracts substring matching above patter for JVM instructions*/
                 int index  = fun.indexOf(":");
                 if(index == -1)
                    continue;
                 else{
               
                    fun = fun.substring(index);
                    index = fun.indexOf(" ");
                    fun = fun.substring(index+1);
                    index = fun.indexOf(" ");
                    if(index == -1)
                      fun = fun.substring(0);
                    else
                      fun = fun.substring(0,index);
                   /*Saving it into map as key value pair*/
                    if(map.containsKey(fun))
                      map.put(fun,map.get(fun)+1);
                    else
                      map.put(fun,1); 
                  
                    
                 } 
                 
               
              }
           
           }
           
           obj.JVMInstCount = map;      
           return obj;
           
           
      
      
      }
      /*Getting number of Class methods  by javap -p command*/
      
      public static int getMethods(List<String> Data){
      
             //ClassesInformation obj = new ClassesInformation();
             String size = Data.get(1);
             
             /*Checking each string for function name regular expression*/
             int sum=0;
             for(int i=2;i<Data.size();i++){
                if(checkFunctionName(Data.get(i)) == true)
                  sum++;
             }
             
             return sum;
      
      }
      
      public static void GetCombinedInfo(List<ClassesInformation> list){
 
 
           try{
             FileWriter fw = new FileWriter("OutputAnswers.txt");
          /*Initalization of ariables for calculation of max,min and avg size of pool*/     
             int maxConstantPoolSize = list.get(0).sizeOfPool,minConstantPoolSize = list.get(0).sizeOfPool,avgMethods = 0;
             double avgPoolSize=0,squarePoolSize=0;
             HashMap<String,Integer> JVMCombInstCount = new HashMap<String,Integer>();
             for(int i =0;i<list.size();i++){
             
             /*Calculates the maximum pool size among class*/
                 ClassesInformation obj = list.get(i);
                 if(maxConstantPoolSize <= obj.sizeOfPool)
                    maxConstantPoolSize = obj.sizeOfPool;
              /*Calculates minimum pool size among class*/     
                 if(minConstantPoolSize >= obj.sizeOfPool)
                    minConstantPoolSize = obj.sizeOfPool;
               
               /*Calculates avg pool size and standard deviation for pool size*/     
                 avgPoolSize += obj.sizeOfPool;
                 squarePoolSize += obj.sizeOfPool*obj.sizeOfPool;
                 avgMethods += obj.CountMethods;
                          
                 for(HashMap.Entry<String,Integer> entry : obj.JVMInstCount.entrySet()){
             /* Gets combine hash map for jvm instructions with their frquency*/    
                     String key = entry.getKey();
                     Integer value = entry.getValue();
                     
                     if(JVMCombInstCount.containsKey(key))
                        JVMCombInstCount.put(key,JVMCombInstCount.get(key)+value);
                     else
                        JVMCombInstCount.put(key,value); 
                     
                 }
                 
                 
                 
             }
             
             avgPoolSize = avgPoolSize/list.size();
             double variance = squarePoolSize/list.size() - avgPoolSize*avgPoolSize;
             /*Sorting by Tree map and compare  class of my own*/ 
             CompareByValue<String> cbv = new CompareByValue<String>(JVMCombInstCount);
             TreeMap<String,Integer> sortbyvalue = new TreeMap<String,Integer>(cbv);
             sortbyvalue.putAll(JVMCombInstCount);
             
             /*
             System.out.println("Average Size of Constant Pool : " + (int)avgPoolSize);
             System.out.println("Max Size of Constant Pool : " + maxConstantPoolSize);
             System.out.println("Min Size of Constant Pool : " + minConstantPoolSize);
             System.out.println("Standarad deviation of Constant Pool : " + Math.sqrt(variance));             
             System.out.println("Average number of methods : " + avgMethods/list.size()); 
             */
             
             fw.write("This is the Extracted Information about Constant Pool Size : \n\n");
             fw.write("Apporximate Average Size of Constant Pool : " + (int)avgPoolSize+"\n");
             fw.write("Max Size of Constant Pool : " + maxConstantPoolSize+"\n");
             fw.write("Min Size of Constant Pool : " + minConstantPoolSize+"\n");
             fw.write("Approximate Standarad deviation of Constant Pool : " + (int)Math.sqrt(variance)+"\n");
             fw.write("-------------------------------------------------------------\n\n");
                          
             fw.write("Approximate Average number of methods in class : " + avgMethods/list.size()+"\n\n");
             fw.write("-------------------------------------------------------------\n\n"); 
             int count=0;
             /*Get top 50 of JVM instructions in terms of occurences*/
             fw.write("This are the top 50 JVM instructions with their frequency : \n\n");
             for(Map.Entry<String,Integer> entry : sortbyvalue.entrySet()) {
                 String key = entry.getKey();
                 Integer value = entry.getValue();

                 fw.write(key + " => " + value + "\n");
                 if(count++>=49)
                   break;
              }
              fw.write("\n-------------------------------------------------------------");
              fw.close();
              //System.out.println(count);
           }catch(IOException e){
              System.out.println(e.getMessage());
           }
      
      }
      
      /*Function which carries out System commands and returns result in ArrayList*/
     public static List<String> runSystemCommand(List<String> commands){
     
            /*Process Builder throws an empty thread exception so we have to catch exception*/
            try{ 
                  /*Instantiation of ProcessBuilder object to run Commands*/
                  ProcessBuilder pb = new ProcessBuilder(commands);
            
                  /*Intialising process variable to start the process*/
                  Process process = pb.start();
          
                 /*Getting Output of Commands that would be on terminal
                 Required in getting Class Paths and Other Information*/  
                 BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
         
                 /*Get the Error if Produced*/
                 BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                 
                 /*Declare to Lists to catch Ouput and Errors if any*/
                 List<String> OutputData = new ArrayList<String>();
                 List<String> ErrorData  = new ArrayList<String>();
                 String s = null;
                 
                 /*Reading Ouput of Command Produces*/
                 while ((s = stdInput.readLine()) != null) {
                        OutputData.add(s);
                 }
                 
                /*Get Errors produced if any*/ 
                while ((s = stdError.readLine()) != null) {
                       ErrorData.add(s);
                }
                
                if(ErrorData.size() == 0){
                   //System.out.println("Command Executed Successfully");
                }
                else{
                   //System.out.println("Some Errors are Prouduced");
                }
                
                return OutputData;
            
            }catch(Exception e){
     
                 System.out.println(e.getMessage());
                 return null;
            }
     
     } 
     public static void main(String args[]){
      
             System.out.print("Enter the link of Jar file You want to Download : ");
             Scanner sc = new Scanner(System.in);
             /*Get Address of link from User*/
             String link_Address = sc.nextLine();
             List<String> commands = getFreshObject();
             /*Process Builder takes list of commands to enter and prints its ouput to Screen
               Command : wget -O Classes.jar adress*/
             System.out.println("Wait till Downloading Gets Complete ---------------------------");
             commands.add("wget");
             commands.add("-O");
             commands.add("Classes.jar");
             commands.add(link_Address);
             /*Calling System Command for Executing*/
             List<String> OutputData = runSystemCommand(commands);
             
             /*We need to Unzip the Contents of Jar File
               Command : unzip -o -d Extract Classes.jar*/
             System.out.println("Unzipping the contents of Jar file in Extract Folder-------------------------");
             commands = getFreshObject();
             commands.add("unzip");
             commands.add("-o");
             commands.add("-d");
             commands.add("Extract");
             commands.add("Classes.jar");
             
             OutputData = runSystemCommand(commands);
             
             /*Get all class Files and their paths to perform diassembler functions
               Use Shell Script pattern matching to get paths recursively of .class
               Command : find Extract/ -path *.class */
             System.out.println("Getting All class file from Extracted folder--------------------");
             commands = getFreshObject();
             commands.add("find");
             commands.add("Extract/");
             commands.add("-path");
             commands.add("*.class");
 
             OutputData = runSystemCommand(commands);
             /*
             System.out.println("This are Paths of Class Found : \n");
             for(int i = 0;i < OutputData.size();i++)
                System.out.println(OutputData.get(i));
             */
                
             /*Traversing each class for further Calculations*/
             List<ClassesInformation> classInfo = new ArrayList<ClassesInformation>();
             
             for(int i = 0;i<OutputData.size();i++){
             
                System.out.println("Retriveing Information for class = " + OutputData.get(i) + "----------------------------");
                commands = getFreshObject();
                /*Command to extract constant pool from class
                  -verbose use to diassmble class in various field*/
                
                commands.add("javap");
                commands.add("-verbose");
                commands.add(OutputData.get(i));
               
                List<String> Data = runSystemCommand(commands);
                ClassesInformation obj = extractInfoComplete(Data);
                obj.ClassName = OutputData.get(i);
                /*
                commands = getFreshObject();
                commands.add("javap");
                commands.add("-c");
                commands.add(OutputData.get(i));
                Data = runSystemCommand(commands);
                
                obj.JVMInstCount = getJVMInfo(Data);
                */
               
    /*Use command for number of methods using javap command -p
      command to show all methods and classes*/
                commands = getFreshObject();
                commands.add("javap");
                commands.add("-p");
                commands.add(OutputData.get(i));
                List<String> DataList = runSystemCommand(commands);
                int CountMethods = getMethods(DataList);
                //obj.ClassName = OutputData.get(i);
                obj.CountMethods = CountMethods;
                //obj.PrintInfo();
                classInfo.add(obj);
                System.out.println("Information Retrieval Completed Successfully");
                System.out.println("--------------------------------------------");
             }
                
             System.out.println("\n\nThis are Paths of Class Found : \n");
             for(int i = 0;i < OutputData.size();i++)
                System.out.println(OutputData.get(i));
              
             GetCombinedInfo(classInfo);
             System.out.println("\n\nAll mentioned Answers are Reported in OutputAnswers.txt");
        }
      
}
