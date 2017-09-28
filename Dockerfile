# WARNING!  I am on a low-bandwidth internet connection and have not been able to build this
# image myself yet.  Almost certainly it won't work, but the ideas are right


# Use as are base image a linux with the java8 runtime already installed
FROM openjdk:8

# Add our application logic and ALL our dependencies into the docker image
ADD  build/distributions/CS5356_Assignment2.tar  /

# The .tar file that gradle builds includes everything in src/main, but we also need
# our appconfig.yml (which is not part of the .tar that gradle builds) so we must
# add it explicitly
ADD appconfig.yml /CS5356_Assignment2/

# Add your GCP Service Account API File to the Docker Image
ADD gc_api_file.json /CS5356_Assignment2/gc_api_file.json
ENV GOOGLE_APPLICATION_CREDENTIALS=/CS5356_Assignment2/gc_api_file.json

# Convenience if we ever want to log into the image and snoop around
WORKDIR /CS5356_Assignment2

# The server is runs on 8080 inside the running container, so we need to expose that port
EXPOSE 8080

# When a new container is created, the server program should be run.
ENTRYPOINT ["/CS5356_Assignment2/bin/CS5356_Assignment2", "server", "appconfig.yml"]
