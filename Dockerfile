FROM openjdk:8
ADD target/uberjar/todo-cljs-api-standalone.jar /todo-cljs-api/app.jar
EXPOSE 3000
CMD ["java", "-jar", "/todo-cljs-api/app.jar"]
