package com.openrangelabs.services.config;

import com.openrangelabs.services.tools.Commons;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

import java.util.Arrays;

@Configuration
@Slf4j
public class RabbitMQConfig {

    private final CachingConnectionFactory cachingConnectionFactory;

    public RabbitMQConfig(CachingConnectionFactory cachingConnectionFactory) {
        this.cachingConnectionFactory = cachingConnectionFactory;
    }

    @Bean
    public RetryOperationsInterceptor retryInterceptor(){
        return RetryInterceptorBuilder.stateless().maxAttempts(3)
                .backOffOptions(2000, 2.0, 100000)
                .recoverer((message, cause) -> {
                    log.error(Arrays.toString(message) + " retries exhausted: "+cause.getMessage());
                    return new RejectAndDontRequeueRecoverer();
                })
                .build();
    }

    @Bean(name = "ListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, cachingConnectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setAdviceChain(retryInterceptor());
        return factory;
    }

    @Bean
    public Declarables createPostVisitorSchema(){
        DirectExchange createUserExchange = new DirectExchange(Commons.CREATE_USER_EXCHANGE);
        DirectExchange dlxExchange = new DirectExchange(Commons.CREATE_USER_DLX_EXCHANGE);
        Queue portalUserQueue = QueueBuilder.durable(Commons.PORTAL_USER_QUEUE).withArgument("x-dead-letter-exchange",Commons.CREATE_USER_DLX_EXCHANGE).withArgument("x-dead-letter-routing-key","portal-user-dlq").build();
        Queue portalUserFailQueue = QueueBuilder.durable(Commons.PORTAL_USER_DLQ_QUEUE).build();
        return new Declarables(
                createUserExchange,
                portalUserQueue,
                portalUserFailQueue,
                dlxExchange,
                new Binding(Commons.PORTAL_USER_DLQ_QUEUE, Binding.DestinationType.QUEUE, Commons.CREATE_USER_DLX_EXCHANGE, "portal-user-dlq", null),
                new Binding(Commons.PORTAL_USER_QUEUE, Binding.DestinationType.QUEUE, Commons.CREATE_USER_EXCHANGE, "portal-user", null)
        );
    }

    @Bean
    public Declarables createSignNowSchema(){
        DirectExchange createUserExchange = new DirectExchange(Commons.SIGNNOW_EXCHANGE);
        DirectExchange dlxExchange = new DirectExchange(Commons.SIGNNOW_DLX_EXCHANGE);
        Queue portalUserQueue = QueueBuilder.durable(Commons.ROSTER_DOCUMENTS_QUEUE).withArgument("x-dead-letter-exchange",Commons.SIGNNOW_DLX_EXCHANGE).withArgument("x-dead-letter-routing-key","roster-user-documents-dlq").build();
        Queue portalUserFailQueue = QueueBuilder.durable(Commons.ROSTER_USER_DOCUMENT_DLQ_QUEUE).build();
        return new Declarables(
                createUserExchange,
                portalUserQueue,
                portalUserFailQueue,
                dlxExchange,
                new Binding(Commons.ROSTER_USER_DOCUMENT_DLQ_QUEUE, Binding.DestinationType.QUEUE, Commons.SIGNNOW_DLX_EXCHANGE, "roster-user-documents-dlq", null),
                new Binding(Commons.ROSTER_DOCUMENTS_QUEUE, Binding.DestinationType.QUEUE, Commons.SIGNNOW_EXCHANGE, "roster-user-documents", null)
        );
    }

    @Bean
    public Declarables createCmdbContactSchema(){
        DirectExchange createCmdbContactExchange = new DirectExchange(Commons.CMDB_CONTACT_EXCHANGE);
        DirectExchange dlxCmdbContactExchange = new DirectExchange(Commons.CMDB_CONTACT_DLX_EXCHANGE);
        Queue portalUserQueue = QueueBuilder.durable(Commons.CMDB_CONTACT_QUEUE).withArgument("x-dead-letter-exchange",Commons.CMDB_CONTACT_DLX_EXCHANGE).withArgument("x-dead-letter-routing-key","cmdb-contact-dlq").build();
        Queue portalUserFailQueue = QueueBuilder.durable(Commons.CMDB_CONTACT_DLQ_QUEUE).build();
        return new Declarables(
                createCmdbContactExchange,
                portalUserQueue,
                portalUserFailQueue,
                dlxCmdbContactExchange,
                new Binding(Commons.CMDB_CONTACT_DLQ_QUEUE, Binding.DestinationType.QUEUE, Commons.CMDB_CONTACT_DLX_EXCHANGE, "cmdb-contact-dlq", null),
                new Binding(Commons.CMDB_CONTACT_QUEUE, Binding.DestinationType.QUEUE, Commons.CMDB_CONTACT_EXCHANGE, "cmdb-contact", null)
        );
    }

    @Bean
    public Declarables createSupportSchema(){
        DirectExchange cgUpdateExchange = new DirectExchange(Commons.SUPPORT_EXCHANGE);
        DirectExchange dlxExchange = new DirectExchange(Commons.SUPPORT_DLX_EXCHANGE);
        Queue cgUpdateFailQueue = QueueBuilder.durable(Commons.SUPPORT_UPDATE_DLQ).build();
        return new Declarables(
                cgUpdateExchange,
                cgUpdateFailQueue,
                dlxExchange,
                new Binding(Commons.SUPPORT_UPDATE_DLQ, Binding.DestinationType.QUEUE, Commons.SUPPORT_DLX_EXCHANGE, "support-update-bonita-dlq", null),
                new Binding(Commons.SUPPORT_UPDATE, Binding.DestinationType.QUEUE, Commons.SUPPORT_EXCHANGE, "support-update-bonita", null)
        );
    }

    @Bean
    public Declarables createMessagingSchema(){
        DirectExchange messagingExchange = new DirectExchange(Commons.MESSAGING_EXCHANGE);
        DirectExchange dlxExchange = new DirectExchange(Commons.MESSAGING_EXCHANGE_DLX);
        Queue emailFailQueue = QueueBuilder.durable(Commons.EMAIL_QUEUE_DLQ).build();
        return new Declarables(
                messagingExchange,
                emailFailQueue,
                dlxExchange,
                new Binding(Commons.EMAIL_QUEUE_DLQ, Binding.DestinationType.QUEUE, Commons.MESSAGING_EXCHANGE_DLX, "email-dlq", null),
                new Binding(Commons.EMAIL_QUEUE, Binding.DestinationType.QUEUE, Commons.MESSAGING_EXCHANGE, "email", null)
        );
    }

    @Bean
    public Declarables createLoggingSchema(){
        DirectExchange loggingExchange = new DirectExchange(Commons.LOGGING_EXCHANGE);
        DirectExchange dlxExchange = new DirectExchange(Commons.LOGGING_DLX_EXCHANGE);
        Queue userLogsQueue = QueueBuilder.durable(Commons.USER_LOGS_QUEUE).withArgument("x-dead-letter-exchange",Commons.LOGGING_DLX_EXCHANGE).withArgument("x-dead-letter-routing-key","logs-user-dlq").build();
        Queue userLogsFailQueue = QueueBuilder.durable(Commons.USER_LOGS_DLQ_QUEUE).build();

        Queue systemLogsQueue = QueueBuilder.durable(Commons.SYSTEM_LOGS_QUEUE).withArgument("x-dead-letter-exchange",Commons.LOGGING_DLX_EXCHANGE).withArgument("x-dead-letter-routing-key","logs-system-dlq").build();
        Queue systemLogsFailQueue = QueueBuilder.durable(Commons.SYSTEM_LOGS_DLQ_QUEUE).build();
        return new Declarables(
                loggingExchange,
                userLogsQueue,
                userLogsFailQueue,
                systemLogsQueue,
                systemLogsFailQueue,
                dlxExchange,
                new Binding(Commons.USER_LOGS_DLQ_QUEUE, Binding.DestinationType.QUEUE, Commons.LOGGING_DLX_EXCHANGE, "logs-user-dlq", null),
                new Binding(Commons.USER_LOGS_QUEUE, Binding.DestinationType.QUEUE, Commons.LOGGING_EXCHANGE, "logs-user", null),
                new Binding(Commons.SYSTEM_LOGS_DLQ_QUEUE, Binding.DestinationType.QUEUE, Commons.LOGGING_DLX_EXCHANGE, "logs-system-dlq", null),
                new Binding(Commons.SYSTEM_LOGS_QUEUE, Binding.DestinationType.QUEUE, Commons.LOGGING_EXCHANGE, "logs-system", null)
        );
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(cachingConnectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

}
