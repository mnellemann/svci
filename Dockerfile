# Replaced the builder image to support serveral architectures.
# Use the Red Hat Universal Base Image (UBI) for a lightweight and secure base image
FROM registry.access.redhat.com/ubi9/openjdk-21:latest
#Create working folder
RUN mkdir /opt/app
# Copy the compiled jar file into the container
COPY ./svci-*.jar /opt/app/svci.jar
# Run the jar file with default values for config file
CMD ["java", "-jar", "/opt/app/svci.jar", "-c", "/opt/app/config/svci.toml"]