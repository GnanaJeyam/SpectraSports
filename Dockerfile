FROM gradle:jdk17-alpine as builder
WORKDIR /spectra
COPY . .
RUN gradle build

FROM gradle:jdk17-alpine
WORKDIR /spectra
COPY --from=builder /spectra/build/libs/spectrasports-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "spectrasports-0.0.1-SNAPSHOT.jar"]