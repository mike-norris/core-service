package com.openrangelabs.services.message;


import org.springframework.stereotype.Repository;

@Repository
public class FlashMessageService {

    /**
    @Autowired
    @Qualifier(MQ_TEMPLATE)
    public AmqpTemplate rabbitTemplate;

    @Autowired
    @Qualifier(MQ_ADMIN)
    public AmqpAdmin mqAdmin;
**/
    public String queueName;
    public String exchangeName = "customer.topic";
    public String routingkeyName;

    public void setQueueName(String name) {
        this.queueName = name;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setRoutingkeyName(String routingkeyName) {
        this.routingkeyName = routingkeyName;
    }

/**
    public Queue loadQueue() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        // Message Expiration
        arguments.put("x-message-ttl",86400000); //86400000 = 24 hours
        // Auto-Expire Queue
        //arguments.put("x-expires",86400000);
        return new Queue(this.queueName, true, false, false, arguments);
    }

    public DirectExchange directExchange() {
        return new DirectExchange(this.exchangeName);
    }

    public TopicExchange topicExchange() {
        return new TopicExchange(this.exchangeName, true, false);
    }

    public void create(String exchangeType) {
        try {
            if (exchangeType.contains("topic")) {
                mqAdmin.declareExchange(this.topicExchange());
            } else {
                mqAdmin.declareExchange(this.directExchange());
            }
        } catch (Exception e) {
            log.info("The exchange "+this.exchangeName+" already exists.");
            log.info(e.getMessage());
            e.printStackTrace();
        }
        try {
            mqAdmin.declareQueue(this.loadQueue());
        } catch (Exception e) {
            log.info("The queue "+this.queueName+" already exists.");
            log.info(e.getMessage());
            e.printStackTrace();
        }
        try {
            mqAdmin.declareBinding(this.binding(exchangeType));
        } catch (Exception e) {
            log.info("The "+exchangeType+" binding "+this.exchangeName+"::"+this.queueName+" with route "+this.routingkeyName+" already exists.");
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    //    @Bean(name = BLOXOPS_MESSAGE_BINDING)
    public Binding binding(String exchangeType) {
        if (exchangeType.contains("topic")) {
            return BindingBuilder.bind(this.loadQueue()).to(this.topicExchange()).with(this.routingkeyName);
        }
        return BindingBuilder.bind(this.loadQueue()).to(this.directExchange()).with(this.routingkeyName);
    }

    public void createRoute(String queueName, String routingKey) {
        this.setQueueName(queueName);
        this.setRoutingkeyName(routingKey);
        this.create("topic");
    }

    public void send(FlashMessage message) {
        String routingkey = "";
        if (message.getOrganizationId() != null) {
            routingkey = message.getOrganizationId().toString();
        }
        if (message.getUserId() != null) {
            routingkey += "." + message.getUserId().toString();
        }
        rabbitTemplate.convertAndSend(this.exchangeName, routingkey, message);
        log.info("Send msg = " + message);

    }
**/
}
