FROM java:8-alpine
MAINTAINER TungDuy <tung.buiduy.91@gmail.com>

ADD target/modus-0.1.0-SNAPSHOT-standalone.jar /modus/modus-0.1.0-SNAPSHOT-standalone.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-DDB_CONNSTR=modus-monolith-c63-dev.c8epalgxmd5k.eu-central-1.rds.amazonaws.com:5432/modus_dev", "-DDB_USERNAME=modus_c63_dev", "-DDB_PASSWORD=modus-c63-dev"]
CMD ["/modus/modus-0.1.0-SNAPSHOT-standalone.jar"]
