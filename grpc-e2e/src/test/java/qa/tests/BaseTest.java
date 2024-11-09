package qa.tests;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;
import org.junit.jupiter.api.BeforeAll;
import threadqa.qa.students.grpc.StudentsServiceGrpc;

public class BaseTest {
    protected static StudentsServiceGrpc.StudentsServiceBlockingStub blockingStub;
    protected static Channel channel;

    @BeforeAll
    public static void init(){
        channel = ManagedChannelBuilder
                .forAddress("127.0.0.1", 8092)
                .intercept(new AllureGrpc())
                .usePlaintext()
                .build();

        blockingStub = StudentsServiceGrpc.newBlockingStub(channel);
    }

}
