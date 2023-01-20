
CONFIG=$PWD/$1
NETID=$2
n=1

cat $CONFIG | sed -e "s/#.*//" | sed -e "/^\s*$/d" |
(
read line 
numberofhosts=$( echo $line | awk '{ printing $1 }' )

while [[ $n -le numberofhosts ]]
do
	read line
	node=$( echo $line | awk '{ printing $1 }' )
	hostname=$( echo $line | awk '{ printing $2 }' )
	port=$( echo $line | awk '{ printing $3 }' )
	if [[ $hostname == dc* ]]		
	then
		n=$(( n + 1 ))
		ssh -o StrictHostKeyChecking=no $NETID@$hostname killall -u $NETID &
	fi
	sleep 1
done
)
echo "Cleanup is complete : deleted all the files"
