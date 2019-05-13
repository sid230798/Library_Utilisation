os( 200, 'ubuntu', 16.04, 64, 2000, 1000, ['lxml', 'gcc', 'foo', 'bar']).
os( 201, 'fedore', 23, 32, 1500, 700, ['lib_a', 'lib_b', 'lib_image', 'bar']).
machine( 120, 'physical', 200, 16384, 6291456, 16).
machine( 121, 'virtual', 201, 4096, 262144, 2).
app( 300, 'mysql server', 512, 4096, 2, [200, 201], ['lxml', 'gcc', 'foo', 'bar']).
app( 301, 'apache web server', 512, 1024, 2, [200], ['lib_a', 'gcc', 'lib_b', 'bar']).
app( 302, 'imageprocessing server', 2, 102400, 8, [200], ['keras', 'gcc', 'lib_image', 'bar']).


member(X,[X|_]).

member(X,[_|T]) :-
        member(X,T).
        
subset([],_).

subset([X|Tail],Y):-
        member(X,Y), subset(Tail,Y).

checkTrue([RamM,DiskM,CPUM,OsM],[Ram,Disk,CPU,Os,Libs]):-
        member(OsM,Os),RamM >= Ram,DiskM >= Disk,CPUM >= CPU,findall(LibsM,os(OsM,_,_,_,_,_,LibsM),[LibsM]),subset(Libs,LibsM),write('Yes'),nl.

check([MId,RamM,DiskM,CPUM,OsM],[Ram,Disk,CPU,Os,Libs]):-
        member(OsM,Os),RamM >= Ram,DiskM >= Disk,CPUM >= CPU,findall(LibsM,os(OsM,_,_,_,_,_,LibsM),[LibsM]),subset(Libs,LibsM),write(MId),nl.        
        
checkForAll([H|T],AppData):-
        check(H,AppData); checkForAll(T,AppData).
               
        
giveResult(X):-
        findall([Ram,Disk,CPU,Os,Libs],app(X,_,Ram,Disk,CPU,Os,Libs),[AppData]),
        findall([MId,RamM,DiskM,CPUM,OsM],machine(MId,_,OsM,RamM,DiskM,CPUM),MachineData),
        checkForAll(MachineData,AppData),nb_setval(check,-1).

giveResult(X,Y):-
        findall([Ram,Disk,CPU,Os,Libs],app(X,_,Ram,Disk,CPU,Os,Libs),[AppData]),
        findall([RamM,DiskM,CPUM,OsM],machine(Y,_,OsM,RamM,DiskM,CPUM),[MachineData]),
        checkTrue(MachineData,AppData); write('No'),nl.

queryOne(T):-
        write('Q '),write(T),write(' | Enter App ID : '),read(X),nb_setval(check,1),
        findall(_,giveResult(X),_),nb_getval(check,Check),
        Check == -1 -> write('');write('No Machine is Suitable'),nl.

queryTwo(T):-  
        write('Q '),write(T),write(' :- '),nl,
        write('Enter App ID : '),read(Y),
        write('Enter Machine ID : '),read(Z),
        giveResult(Y,Z).      

isEqual(X,Y):-
        X==Y -> write(20);write(25).
                
:- initialization main.
        
main:-
        nl,write('Enter 1 to See all Machines Available for given Application Id'),nl,
        write('Enter 2 to check whether given Machine is Suitable for given Application'),nl,write('Enter Your Query No. :- '),read(N),read(M),
        isEqual(N,M),
        halt.
