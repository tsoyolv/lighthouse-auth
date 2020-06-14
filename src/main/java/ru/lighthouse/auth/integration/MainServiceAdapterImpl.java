package ru.lighthouse.auth.integration;

import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

@Service
public class MainServiceAdapterImpl implements MainServiceAdapter {

    private final MainServiceFeignClient mainServiceFeignClient;

    public MainServiceAdapterImpl(MainServiceFeignClient mainServiceFeignClient) {
        this.mainServiceFeignClient = mainServiceFeignClient;
    }

    @Override
    public FutureTask<UserDto> getOrCreateUser(UserDto userDto) {
        Callable<UserDto> task = () -> mainServiceFeignClient.createOrUpdateUser(userDto);
        return getFuture(task);
    }

    private FutureTask<UserDto> getFuture(Callable<UserDto> task) {
        FutureTask<UserDto> future = new FutureTask<>(task);
        new Thread(future).start();
        return future;
    }
}
