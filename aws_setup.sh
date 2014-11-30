#bin/bash
HOME=':~'
declare -a arr=('ubuntu@54.149.27.202'  'ubuntu@54.69.25.125')

for i in 0 1; do
     scp -i golubensen.cer target/paxos-0.0.1.jar ${arr[$i]}$HOME
done

for i in 0 1; do
     osascript -e "tell application \"Terminal\"" -e "tell application \"System Events\" to keystroke \"n\" using {command down}" -e "do script \"ssh -i golubensen.cer ${arr[$i]} java -cp paxos-0.0.1.jar edu.ucsb.cs.Main $i  \" in front window" -e "end tell" > /dev/null
done