FROM openjdk:8

ADD target/uberjar/todo-cljs-api.jar /cci-demo-clojure/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/cci-demo-clojure/app.jar"]
