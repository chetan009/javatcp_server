FROM openjdk
COPY src /opt/app
WORKDIR /opt/app
RUN ["mvn install"]
RUN ["cd target; java -jar tcp_test-1.0-SNAPSHOT.jar"]
