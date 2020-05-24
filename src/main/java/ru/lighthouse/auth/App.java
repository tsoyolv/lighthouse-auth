package ru.lighthouse.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Value("${service.instance.id}")
    private int instanceId;

    @GetMapping("/instance-id")
    public Integer getServiceInstanceId() {
        return instanceId;
    }

    @GetMapping("/local-ip")
    public Instance getLocalIp() throws UnknownHostException {
        String localIp = InetAddress.getLocalHost().getHostAddress();
        return new Instance(localIp, instanceId);
    }

    @GetMapping("/testservice")
    public String helloGradle() {
        return "Hello auth!";
    }

    private class Instance {
        private String localIp;
        private int instanceId;

        public Instance() {
        }

        public Instance(String localIp, int instanceId) {
            this.localIp = localIp;
            this.instanceId = instanceId;
        }

        public String getLocalIp() {
            return localIp;
        }

        public void setLocalIp(String localIp) {
            this.localIp = localIp;
        }

        public int getInstanceId() {
            return instanceId;
        }

        public void setInstanceId(int instanceId) {
            this.instanceId = instanceId;
        }
    }
}