FROM java:8

RUN git clone https://github.com/rubenchevez195/scala.git

EXPOSE 8080

CMD cd scala && java -jar ./server.jar