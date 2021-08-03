# The line below states we will base our new image on the Latest Official Ubuntu 
FROM adoptopenjdk/openjdk8

#
# Update the image to the latest packages
#RUN apt-get update && apt-get upgrade -y

#WORKDIR /demo

COPY play-demo demo
COPY secure-connect-scalac-twitter.zip demo/secure-connect-scalac-twitter.zip

RUN chmod +x ./demo
RUN chmod +x ./demo/secure-connect-scalac-twitter.zip

EXPOSE 9000

CMD ["./demo/bin/demo"]

# Run the web service on container startup.
#CMD ["java", "-Dplay.http.secret.key=yrxOrf`A1W_AHGbnHfpBVRL8DBhNZeWVe4DtV_]Mr2`fZ>zW^f5FMdUMuP2@gsf/", "-jar", "demo.jar"]
