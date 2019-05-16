package org.camunda.bpm.getstarted.chargecard;

import org.camunda.bpm.client.ExternalTaskClient;

import java.util.logging.Logger;

public class ChargeCardWorker {
    private final static Logger LOGGER = Logger.getLogger(ChargeCardWorker.class.getName());

    /**
     *
     * 通过 http://localhost:8080/engine-rest/process-definition/key/payment-retrieval/start rest api访问
     * 其中 "payment-retrieval" 是bpmn文件的文件id
     * @param args
     */
    public static void main(String[] args) {
        // "engine-rest"在此处定义,已经启动后,通过
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // long polling timeout
                .build();

        // subscribe to an external task topic as specified in the process
        // "charge-card" 对应bpmn文件里service task的topic
        client.subscribe("charge-card")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here

                    // Get a process variable
                    String item = (String) externalTask.getVariable("item");
                    Long amount = (Long) externalTask.getVariable("amount");
                    LOGGER.info("1:Charging credit card with an amount of '" + amount + "'€ for the item '" + item + "'...");

                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();

        // subscribe to an external task topic as specified in the process
        // "charge-card2" 对应payment.bpmn文件里service task "Charge Credit Card2"的topic
        // bpmn文件路径 D:\data\workspace\IDEA20191\chargecardworker\src\main\resources\payment.bpmn
        client.subscribe("charge-card2")
                .lockDuration(1000) // the default lock duration is 20 seconds, but you can override this
                .handler((externalTask, externalTaskService) -> {
                    // Put your business logic here

                    // Get a process variable
                    String item = (String) externalTask.getVariable("item");
                    Long amount = (Long) externalTask.getVariable("amount");
                    LOGGER.info("2:Charging credit card with an amount of '" + amount + "'€ for the item '" + item + "'...");

                    // Complete the task
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}