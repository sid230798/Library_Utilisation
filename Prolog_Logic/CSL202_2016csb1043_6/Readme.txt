----------------------------------------------------------------

Author : Siddharth Nahar
Entry No : 2016csb1043
Use : Learning YAML parser,Generators in python and Prolog Rules.
Date : 24/4/18

-----------------------------------------------------------------

For Run :

User~PATH$ python CSL202_2016csb1043_6.py YAML_FILE_PATH

Input :

1. It asks to continue with file provided or Want to Add new File Path
2. When Query Part is to be Executed ,Enter Value as Follows:

        -- After each value put a '.' for Prolog script to continue

3. Asks for Application  ID or MachineID as required .
        
Output :

1. Prints Possible MachineID or Prints No such machine.
2. Prints Yes or no for given machine id and Application Id.

--------------------------------------------------------------------------------------------------------

Sample Run : 

siddharth@siddharth-Latitude-E6430:~/CSL202_Lab/CSL202_2016csb1043_6$ python CSL202_2016csb1043_6.py Conf.yaml

Press 1 to Continue with the YAML file Provided
Press 2 to Enter File Path for new YAML file
Press 3 to Quit
Enter Your Option : 1

Enter 1 to See all Machines Available for given Application Id
Enter 2 to check whether given Machine is Suitable for given Application
Enter Your Query No. :- 1.
Q 1 | Enter App ID : |: 300.
120
-------------------------------------

Press 1 to Continue with the YAML file Provided
Press 2 to Enter File Path for new YAML file
Press 3 to Quit
Enter Your Option : 1

Enter 1 to See all Machines Available for given Application Id
Enter 2 to check whether given Machine is Suitable for given Application
Enter Your Query No. :- 2.
Q 2 :- 
Enter App ID : |: 300.
Enter Machine ID : |: 120.
Yes
-------------------------------------

Press 1 to Continue with the YAML file Provided
Press 2 to Enter File Path for new YAML file
Press 3 to Quit
Enter Your Option : 3
siddharth@siddharth-Latitude-E6430:~/CSL202_Lab/CSL202_2016csb1043_6$
---------------------------------------------------------------------------------------------------------

Files_Present : 

1. CSL202_2016csb1043_6.py Takes YAML file as input,Creates Facts and append in Facts_Gen.pl
        It also appends Rules from Rules.txt.
        
2. Rules.txt this File contains All Rules for Prolog file.
3. Conf.yaml is sample YAML file created by me.

-----------------------------------------------------------------------------------------------------------

Working : 

        -- Use yaml library to parse file.
        -- To prevent overwrite of Entities ,Generator is Used and Segregated into different lists.
        -- Append Lists as Facts for Prolog file.
        -- Use find_all functionality in Prolog to get Lists of Lists of Machines,Other Facts
        -- Use Recursion to create logics of Check as discussed in Design file.
        
------------------------------------------------------------ 
