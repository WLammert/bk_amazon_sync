FROM gcr.io/distroless/java:11-debug

COPY build/libs/bk_amazon_sync.jar /sync.jar

ENTRYPOINT ["java","-jar","/sync.jar"]