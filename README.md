# Motion Detection Application
Main goal of this app is to detect any motion with Raspberry PI 4 PIR-sensor
and record a video of the action.

## App
Main java application. Firstly, receiving messages from MQTT-broker and lays out data
in DB storage. Secondly, providing REST API that allows users to check all alarms
occurred.

## Mosquitto
The most popular MQTT-broker image. When motion has been detected, raspberry
send message on this broker.

## Minio
An open-source object storage solution.
Receives files (especially videos from raspberry devices). 