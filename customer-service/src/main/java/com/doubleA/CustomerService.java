package com.doubleA;

import com.amqp.RabbitMQMessageProducer;
import com.doubleA.fraud.FraudClient;
import com.doubleA.fraud.FraudResponse;
import com.doubleA.notification.NotificationClient;
import com.doubleA.notification.NotificationRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public record CustomerService(
        CustomerRepository customerRepository,
        RestTemplate restTemplate,
        FraudClient fraudClient,
        NotificationClient notificationClient,
        RabbitMQMessageProducer rabbitMQMessageProducer
) {

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .build();

        customerRepository.saveAndFlush(customer);

        FraudResponse fraudResponse = fraudClient.isFraudster(customer.getId());

        if (fraudResponse.isFraudster()) {
            throw new IllegalStateException("Fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi! %s, welcome to our...", customer.getFirstname())
        );
        rabbitMQMessageProducer.publisher(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );

    }
}
