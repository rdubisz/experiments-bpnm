# experiments-bpmn
Business Process Model and Notation exercise

## Requirements

- Java >= 17
- Maven

## Building

`mvn clean install`

## Running

`java -jar target/experiments-bpmn-1.0.0-SNAPSHOT-jar-with-dependencies.jar <start-node-id> <end-node-id>`

for example:
`java -jar target/experiments-bpmn-1.0.0-SNAPSHOT-jar-with-dependencies.jar approveInvoice invoiceProcessed`

