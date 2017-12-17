[![CircleCI](https://circleci.com/gh/amfleming/skeleton.svg?style=svg)](https://circleci.com/gh/amfleming/skeleton)


Receipt Management
============
An application for adding, tagging and managing receipts. It leverages Google Vision API to upload paper receipts and provides a simple foundation for building scalable RESTful services.

To build and run with docker:
1. Run `./gradlew distTar` _this tells gradle to make a .tar file containing the java application code and all dependencies_
2. Run `docker build -t myapp .` _this runs the Dockerfile, and builds an image tagged with `myapp`.  See all images with `docker images`_
3. Run `docker run -p 80:8080 myapp` _this runs the `myapp` image, routing port 80 on **Your Machine** to port 8080 in **the container**_
