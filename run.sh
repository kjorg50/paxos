rm -f bank*
mvn clean install
cp src/main/recources/log4j.properties target/lib
java -cp target/paxos-0.0.1.jar:target/lib/*:target/lib edu.ucsb.cs.Main $1
