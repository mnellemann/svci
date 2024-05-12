# Using ubi9/openjdk to support serveral architectures.
FROM registry.access.redhat.com/ubi9/openjdk-21:latest
#Create working folder
RUN mkdir /opt/app
# Copy the compiled jar file into the container
COPY ./svci-latest.jar /opt/app/
# Run the jar file with default values for config file
CMD ["java", "-jar", "/opt/app/svci-latest.jar", "-c", "/opt/app/config/svci.toml"]
