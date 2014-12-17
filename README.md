# Paxos

Implementing a bank account management system that is replicated to many sites using the Paxos consensus protocol.

## Usage
1. `mvn clean install`
2. `cp src/main/recources/log4j.properties target/lib` This will make sure the log4j properties are read from the classpath
3. `java -cp target/paxos-0.0.1.jar:target/lib/*:target/lib edu.ucsb.cs.Main <node_num>` See the *edu.ucsb.cs.MessengerConf.java* file for details on the servers and corresponding node numbers

For easy copy/pasta
```
mvn clean install
cp src/main/recources/log4j.properties target/lib
java -cp target/paxos-0.0.1.jar:target/lib/*:target/lib edu.ucsb.cs.Main
```

## Scripts
For clean startup
```
sh run.sh <node_num>
```
For restart only (keep old bank)
```
sh restart.sh <node_num>
```
