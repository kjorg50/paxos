wget http://mirror.olnevhost.net/pub/apache/zookeeper/stable/zookeeper-3.4.6.tar.gz
gunzip zookeeper-3.4.6.tar.gz 
tar -xvf zookeeper-3.4.6.tar 
sudo mv zookeeper-3.4.6 /usr/local/zookeeper
mkdir /usr/local/zookeeper/data
chown -R ubuntu /usr/local/zookeeper/
export PATH=$PATH:/usr/local/zookeeper/bin
sudo vi /usr/local/zookeeper/conf/zoo.cfg
sudo su
chown -R ubuntu /var/lib/
exit
sudo apt-get update
sudo apt-get install default-jdk

zkServer.sh start
