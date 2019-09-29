
ROOT_PATH=`dirname $0`
cd $ROOT_PATH
java -cp ./lib/*:./zookeeper-monitor-0.0.1-SNAPSHOT.jar com.flynn.zk.App
