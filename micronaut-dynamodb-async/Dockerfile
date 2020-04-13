# Use graalvm native image
FROM oracle/graalvm-ce:20.0.0-java11

# Copy the artifact into the container
COPY target/dmicronaut-dynamodb-async-*.jar /srv/service.jar

# Run the artifact and expose the default port
WORKDIR /srv

ENTRYPOINT [ "java", \
    "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+ExitOnOutOfMemoryError", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "service.jar"]

EXPOSE 8080