'''

Author : Siddharth Nahar
Entry No : 2016csb1043
Use : Using YAML file Parser , and Prologe Language Implementation
Date : 23/4/18

'''

import yaml
import sys
import re
import os

#Function To genreate Facts and Append Rules in File
def GetFileWritten(FILE_PATH):

        with open(FILE_PATH, 'r') as f:
                data = f.read()
                #data = data.replace('\n\n', '\n---\n')
                data = re.sub('\n[ \t\r\f]*\n','\n---\n',data)
                
        #Loading Generator type by load_all        
        Dict = yaml.load_all(data)

        '''
        Creating three Different List
        OS,Machine,Software APP values

        '''
        OS = []
        Machine = []
        App = []

        #Appending Data of yaml file in list
        for x in Dict:
                for key,value in x.items():
                        if(key == 'OS'):
                                OS.append(value)
                        elif(key == 'Machine'):
                                Machine.append(value)
                        else:
                                App.append(value)
                                

        File_Write = open('Facts_Gen.pl','w')

        #Appending Correct Values in String
        '''

        Seperating Specific Fields Accordingly
        Appending lines in file
        Form LKist Extracted in OS
        '''
        for Obj in OS:
                line = 'os( '
                for key,value in Obj.items():
                        if(key == 'limits'):
                                for k,v in value.items():
                                        line = line + str(v)+', '
                        elif(key == 'arch'):
                                Bits = re.findall(r'\b\d+\b',value)
                                line = line + str(Bits[0])+', '
                        elif(key == 'name'):
                                line = line+"'"+value+"'"+", "        
                        else:                
                                line = line + str(value)+', '
                              
                
                line = line[:-2]+').\n'
                line = line.lower()
                File_Write.write(line)
                
        '''

        Extract field from list in Machine
        Append as Strings

        '''
        for Obj in Machine:
                line = 'machine( '
                for key,value in Obj.items():
                        if(key == 'RAM' or key == 'disk' or key == 'CPU'):
                                Mem = re.findall(r'[A-Za-z]+',value)[0]
                                #print(key,Mem)
                                value = int(re.findall(r'\b\d+\b',value)[0])
                                if(Mem == 'KB'):
                                        value = value/1024
                                elif(Mem == 'GB'):
                                        value = value*1024
                                elif(Mem == 'TB'):
                                        value = value*1024*1024
                                else:
                                        value = value
                                        
                                line = line + str(value) +', '
                        elif(key == 'type'):
                                line = line+"'"+value+"'"+", "
                        else:        
                                line = line + str(value) + ', '
                        
                line = line[:-2]+').\n'
                line = line.lower()
                File_Write.write(line) 
                
        for Obj in App:
                line = 'app( '
                for key,value in Obj.items():
                        if(key == 'requires_hardware'):
                                for k,v in value.items():
                                        #print(k,v)
                                        Mem = re.findall(r'[A-Za-z]+',v)[0]
                                        #print(key,Mem)
                                        value_n = int(re.findall(r'\b\d+\b',v)[0])
                                        if(Mem == 'KB'):
                                                value_n = value_n/1024
                                        elif(Mem == 'GB'):
                                                value_n = value_n*1024
                                        elif(Mem == 'TB'):
                                                value_n = value_n*1024*1024
                                        else:
                                                value_n = value_n
                                                
                                        line = line + str(value_n) +', '
                                        #print(line)
                        elif(key == 'requires_software'):
                                for k,v in value.items():
                                        line = line + str(v) +', '
                        elif(key == 'name'):
                                line = line+"'"+value+"'"+", "               
                        else:
                                line = line + str(value) + ', '
                                #print(line)
                                
                line = line[:-2]+').\n'
                line = line.lower()
                File_Write.write(line)
                
        File_Write.write('\n\n')
        '''
        Created Rules File which will append Rules to file
        Appending it

        '''        
        with open('Rules.txt', 'r') as f:
                rules = f.read()
                File_Write.write(rules)
                
        File_Write.close()
#Function End        
        
# Getting File Path from Command line
FILE_PATH = sys.argv[1]

'''
As Yaml file overwrites same data
We will Replace newline with ---

'''

count = 1

while(1):

        print('\nPress 1 to Continue with the YAML file Provided\nPress 2 to Enter File Path for new YAML file\nPress 3 to Quit')
        
        n = input('Enter Your Option : ')
        
        if(int(n) == 1):
                if(count == 1):
                        GetFileWritten(FILE_PATH)
                
        elif(int(n) == 2):
                FILE_PATH = input('Enter File_Path for modified file : ')
                GetFileWritten(FILE_PATH)
        else:
                break        
        count = count+1
        
        #Running System Command through Python for swipl
        os.system('swipl Facts_Gen.pl')
        print('-------------------------------------')
