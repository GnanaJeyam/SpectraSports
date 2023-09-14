FROM gradle:jdk20-jammy as builder
WORKDIR /spectra
COPY . .
RUN gradle build

FROM openjdk:19-alpine
WORKDIR /spectra
COPY --from=builder /spectra/build/libs/spectrasports-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "spectrasports-0.0.1-SNAPSHOT.jar"]