./gradlew clean
./gradlew bootJar
mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)
docker build -t lighthouse-auth-service .
