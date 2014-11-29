#bin/bash
for i in 1 2 3 4 5; do
        osascript -e "tell application \"Terminal\"" -e "tell application \"System Events\" to keystroke \"n\" using {command down}" -e "do script \"cd /Users/nevena/projects/paxos; java -cp target/paxos-0.0.1.jar edu.ucsb.cs.Main $i\" in front window" -e "end tell" > /dev/null
done