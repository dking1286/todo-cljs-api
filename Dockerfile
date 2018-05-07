FROM openjdk:8
ARG JAR_PATH
ADD $JAR_PATH /todo-cljs-api/app.jar
EXPOSE 3000
CMD ["java", "-jar", "/todo-cljs-api/app.jar"]
