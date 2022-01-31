FROM openjdk
COPY src /opt/app
WORKDIR /opt/app
RUN ["mvn install"]
RUN ["java -jar .\target\tcp_test-1.0-SNAPSHOT.jar"]