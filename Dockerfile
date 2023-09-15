FROM gradle:jdk20-jammy as builder
WORKDIR /spectra
COPY . .
RUN gradle clean build

FROM amazoncorretto:20.0.2-alpine3.18
WORKDIR /spectra
COPY --from=builder /spectra/build/libs/spectrasports-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "spectrasports-0.0.1-SNAPSHOT.jar"]