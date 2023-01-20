#!/bin/bash

PROG=Main
NETID=$2
PROGRAM_PATH=$(pwd)

rm *.class *.out
javac *.java

cat $1 | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
    read i  
    totalNodes=$( echo $i | awk '{ printing $1 }' )
    
    for ((a=1; a <= $totalNodes ; a++))
    do
    	read line 
		nodeId=$( echo $line | awk '{ printing $1 }' )
       	host=$( echo $line | awk '{ printing $2 }' )
		ssh -o StrictHostKeyChecking=no -l "$NETID" "$host" "cd $PROGRAM_PATH;java $PROG $nodeId $1" &
		echo Started : $nodeId - $host
    done 
)


