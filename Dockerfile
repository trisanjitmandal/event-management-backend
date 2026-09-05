FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

RUN cp target/*.jar app.jar

EXPOSE 8080

CMD ["sh", "-c", "getent hosts db.typstbjeouuirrhgerpm.supabase.co; java -jar app.jar"]