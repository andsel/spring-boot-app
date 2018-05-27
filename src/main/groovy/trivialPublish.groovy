//@Grab(group='org.fusesource.mqtt-client', module='mqtt-client', version='1.5')
//
//import org.fusesource.mqtt.client.BlockingConnection
//import org.fusesource.mqtt.client.MQTT
//import org.fusesource.mqtt.client.QoS
//
//// usage groovy trivialPublish.groovy  localhost
//
//String host = args[0]
//String topic = "/topic"
//if (args.length > 1) {
//    topic = args[1]
//}
//println "publish script to topic ${topic} on host ${host}"
////start a publisher
//MQTT mqtt2 = new MQTT()
//mqtt2.setHost(host, 1883)
//BlockingConnection publisher = mqtt2.blockingConnection()
//publisher.connect()
//println "publisher connected"
//
//publisher.publish(topic, 'Hello world again!!'.bytes, QoS.AT_MOST_ONCE, false)
//println "publisher published"
//
//println "shutdown publisher"
//publisher.disconnect()

@GrabResolver(name='Paho', root='https://repo.eclipse.org/content/repositories/paho-releases/')
// @Grab(group='org.eclipse.paho', module='mqtt-client', version='0.4.0')
@Grab(group='org.eclipse.paho', module='org.eclipse.paho.client.mqttv3', version='1.2.0')

import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

if (args.length < 1) {
    println "Usage: groovy publish <host> [clientId] [message]"
    exit 1
}

String host = args[0]

String clientId = "PublisherClient"
if (args.length >= 2) {
    clientId = args[1]
}

String messageContent = 'Hello world!!'
if (args.length >= 3) {
    messageContent = args[2]
}

MqttMessage message = new MqttMessage(messageContent.bytes)
message.setQos(0)

//String tmpDir = System.getProperty("java.io.tmpdir")
//MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(tmpDir)
MemoryPersistence persistence = new MemoryPersistence()
MqttClient client = new MqttClient("tcp://${host}:1883", clientId, persistence)
client.connect()
print "publishing at QoS 0"
client.publish('log', 'Hello world!!'.bytes, 0, false)

//client.publish('/exit', 'Exit'.bytes, 0, false)
client.disconnect()
println "disconnected"
client.close()