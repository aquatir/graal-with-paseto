FROM oracle/graalvm-ce:19.3.1 as graalvm
RUN gu install native-image

COPY . /home/app/app
WORKDIR /home/app/app

RUN native-image \
  --verbose \
  -H:+TraceClassInitialization \
  -H:+ReportExceptionStackTraces \
  -H:+PrintClassInitialization \
  --no-server \
  --no-fallback \
  --allow-incomplete-classpath \
  --report-unsupported-elements-at-runtime \
  --initialize-at-build-time=jnr.ffi.Platform \
  -cp build/libs/app-*-all.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
RUN apk --update add gcc make g++ zlib-dev
RUN wget https://download.libsodium.org/libsodium/releases/LATEST.tar.gz && tar -xvf LATEST.tar.gz
WORKDIR /libsodium-stable
RUN ./configure && make && make check && make install
WORKDIR /
COPY --from=graalvm /home/app/app/app /app/app
ENTRYPOINT ["/app/app"]
