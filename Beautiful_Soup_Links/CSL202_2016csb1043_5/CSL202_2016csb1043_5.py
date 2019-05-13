#!/usr/bin/env python3
# -*- coding: utf-8 -*-
'''
Created on Fri Mar 30 13:02:52 2018

Author: Siddharth Nahar
Entry No : 2016csb1043
Use : Beautiful Soup Library Use and Basic Coding in Python

'''
# Importing BeautifulSoup library from bs4 folder
# Loading important Libraries
import sys
import re
import operator
from bs4 import BeautifulSoup

#Taking first Argument as File Path
File_Path = sys.argv[1]
OutFile_Path = ""

ind = File_Path.rfind("/")
if(ind == -1):
        OutFile_Path = File_Path+".dedup"
else:
        OutFile_Path = File_Path[ind+1]+".dedup"

        
# Opening File passed as Command Line argument
File_Ptr = open(File_Path,'r')

#Second File Pointer as beautiful soup doesnt allow two soup for same file
File_Ptr2 = open(File_Path,'r')

# html5lib parser used
Soup = BeautifulSoup(File_Ptr,'html5lib')
Soup_Final = BeautifulSoup(File_Ptr2,'html5lib')
#print(len(Soup_Final('a')))

Soup_Tag = Soup.find_all("a")

Map_Links = dict()

'''
@Saving link as key in map
@Storing number of duplicate links 

'''
for line in Soup_Tag:
        
        link =  line['href']
        #print(link)
        if(link in Map_Links):
                Map_Links[link] += 1
                #print(link)
        else:
                Map_Links[link] = 1
                #print(link)
 

                
'''
@Declaring Different Varables for further use
@Set_dict map is used to store duplicates links and their LineNum
@Map_LineIndices stroe all links with their Line Indices 

'''               
set_dict = dict()
Map_LineIndices = dict()
content_dict = dict()
#Print line Numbers with Duplicate Links 
File_Ptr = open(File_Path,'r')          
check = 0
i=0
CountLinksRemoved = 0

'''

@Enumerate to get LineNumbers of file
@Pattern to get links line from file
@Check wether given link has duplicates then only insert
'''
for LineNum,Line in enumerate(File_Ptr,1):

        Pattern = 'href\s*=\s*"[^"]*"'
        match   = re.findall(Pattern,Line)
        if(match):
        
                index = match[0].index('"')
                key = match[0][index+1:-1]
                Map_LineIndices[LineNum] = i
                #Content_String = Soup('a')[i].contents[0]
                content_dict[LineNum] = Soup('a')[i].contents[0].strip();
                i = i + 1
                #print(key)
                if(Map_Links.get(key) > 1):
                        check = 1
                        set_dict[LineNum] = key
                        #print(str(i)+". "+key+" at Line "+str(LineNum))
                        

'''
@Check if No duplicates are found
@Get the required Input from User as given and Performs the Task

'''
if(check == 1):
        print("Found Duplicates as Follows\n")
        
        #Sorted_x is used to sort set_dict according to links for 
        #Output Sementics
        
        sorted_x = sorted(set_dict.items(), key=operator.itemgetter(1))
        i=1
        for key in sorted_x:
                print(str(i)+". "+key[1]+" \" "+content_dict[key[0]]+" \""+" at Line "+str(key[0]))
                i = i+1
                
        print("\nEnter A for retaining all, OR \nEnter F to keep in a set of dupicates, OR\nEnter the serial numbers (separated by commas) of the links to keep.")
        Input = input("\nYour Selection : ")
        fileIn = open(OutFile_Path,'w')
        
        #For Option A Keep File as it is
        if(Input == "A"):
                fileIn.write(Soup.prettify())
                print("\n0 Links Removed. Output Written to "+OutFile_Path)
        
        #For Option F keep Onply first links in duplicate sets        
        elif(Input == "F"):
        
                Decompose = dict()
                Soup_a = Soup.find_all("a")
                index=0
                #del_a as it takes dynamic index it must be kept updated
                del_a=0
                
                #Check for all links in soup if duplicate are present and delte all Duplicates
                for line in Soup_a:
                        if(line['href'] in Decompose):
                                
                                Soup_Final('a')[index-del_a].decompose()
                              
                                
                                del_a = del_a + 1
                        else:
                                Decompose[line['href']] = 1
                                
                        index = index + 1
                
                #Write the Html 5 to File        
                fileIn.write(Soup_Final.prettify())
                print("\n"+str(del_a)+" Links Removed. Output Written to "+OutFile_Path)
                
        #Elese Option for keep the links by index        
        else:
                del_a = 0
                for i,line in enumerate(sorted_x,1):
                        
                        if(Input.find(str(i)) == -1):
                                index = Map_LineIndices[line[0]]
                                
                                
                                if((index-del_a) < len(Soup_Final('a'))):
                                        Soup_Final('a')[index-del_a].decompose()
                                        del_a = del_a + 1
                               
                                            

                fileIn.write(Soup_Final.prettify())                
                print("\n"+str(del_a)+" Links Removed. Output Written to "+File_Path+".deedup")
                                
                                
                        
                
                
else:

        print("No Duplicates Found")
        
        
                
        
      

