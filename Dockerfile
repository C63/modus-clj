FROM java:8-alpine
MAINTAINER TungDuy <tung.buiduy.91@gmail.com>

ENV DB_CONNSTR jdbc:postgresql://modus-monolith-c63-dev.c8epalgxmd5k.eu-central-1.rds.amazonaws.com:5432/modus_dev
ENV DB_USERNAME modus_c63_dev
ENV DB_PASSWORD modus-c63-dev
ADD target/modus-0.1.0-SNAPSHOT-standalone.jar /modus/modus-0.1.0-SNAPSHOT-standalone.jar

EXPOSE 8080

CMD ["java", "-jar", "/modus/modus-0.1.0-SNAPSHOT-standalone.jar"]
