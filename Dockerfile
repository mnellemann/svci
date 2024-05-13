# Using eclipse-temurin with JRE to support several architectures.
FROM arm64v8/eclipse-temurin:21-jre
#Create working folder
RUN mkdir /opt/app
# Copy the compiled jar file into the container
COPY ./svci-latest.jar /opt/app/
# Run the jar file with default values for config file
CMD ["java", "-jar", "/opt/app/svci-latest.jar", "-c", "/opt/app/config/svci.toml"]
