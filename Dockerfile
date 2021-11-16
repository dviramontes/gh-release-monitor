FROM clojure:tools-deps
ARG ENVIRONMENT
ENV ENVIRONMENT=$(ENVIRONMENT:-prod)
RUN mkdir -p /app
WORKDIR /app
COPY . ./
RUN make jar
CMD java -jar target/api.main-0.1.0-SNAPSHOT.jar

EXPOSE 8080