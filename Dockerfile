FROM openjdk:11
COPY ./streams.config-demo-1.0-SNAPSHOT.zip /
RUN unzip streams-config-demo-1.0-SNAPSHOT.zip
CMD [ "/streams-config-demo-1.0-SNAPSHOT/bin/streams-config-demo" ]