# Pf4jBulkSaveApiPlugin
Pf4j Bulk Save API Plugin

This bulk save api plugin involves only gate microservice.

     1. Gate

There are 2 ways of deploying this plugin in the spinnaker.

## Method 1

   #### Tasks list

   - [x] Run `./gradlew clean fatJar`.
   - [x] Copy the `batchUpdate-gate-api/build/libs/com.opsmx.gate.bulksave.enabled.plugin.jar` into your spinnaker gate plugin root repository.
         Default spinnaker's microservice plugins root directory is `<opt>/<microservice>/plugins/`. eg:- (opt/gate/plugins).
   - [x] Configure the Spinnaker gate service. Put the following in the gate-local.yml to enable the plugin and configure the extension.
   
          1.   Gate configuration
Adding the following to your gate.yml or ~/.hal/default/profiles/gate-local.yml config will load and start the latest Bulk Save API plugin during app startup.
```
spinnaker:
  extensibility:
    plugins:
      com.opsmx.gate.bulksave.enabled.plugin:
        enabled: true
        api-extension.config:
          id: bulksave
          timeout: 30
          front50Url: http://localhost:8080
```

## Check the logs to confirm the plugin started successfully:

   #### Gate logs
```
2021-05-11 05:30:35.488  INFO 16643 --- [           main] org.pf4j.DefaultPluginManager            : PF4J version 3.2.0 in 'deployment' mode
2021-05-11 05:30:35.823  WARN 16643 --- [           main] c.n.s.kork.version.ServiceVersion        : Unable to determine the service version, setting it to unknown
2021-05-11 05:30:37.546  INFO 16643 --- [           main] c.n.s.config.PluginsAutoConfiguration    : Enabling spinnaker-official and spinnaker-community plugin repositories
2021-05-11 05:30:37.939  INFO 16643 --- [           main] org.pf4j.AbstractPluginManager           : Plugin 'com.opsmx.gate.bulksave.enabled.plugin@1.0.0' resolved
2021-05-11 05:30:40.613  INFO 16643 --- [           main] org.pf4j.AbstractPluginManager           : Start plugin 'com.opsmx.gate.bulksave.enabled.plugin@1.0.0'
2021-05-11 05:30:40.683  INFO 16643 --- [           main] c.o.s.g.plugins.GateBulkSaveApiPlugin    : GateBulkSaveApiPlugin plugin start. 

```

## Method 2

   To be added.
   
   
## Check the gate rest api endpoint to confirm whether the plugin endpoint is working successfully:

   #### Gate GET Method rest endpoint check
```
Rest Endpoint : GET Method : http://<gate-url>:8084/extensions/bulksave/batch

Rest Endpoint Result : {}

```

 #### Gate POST Method Bulk Save rest endpoint check
```
Rest Endpoint : POST Method : http://<gate-url>:8084/extensions/bulksave/batchUpdate

                POST Body : [
  {
    "keepWaitingPipelines": false,
    "limitConcurrent": true,
    "application": "tst004",
    "spelEvaluator": "v4",
    "name": "pipe7",
    "stages": [
      {
        "requisiteStageRefIds": [],
        "name": "Wait",
        "refId": "1",
        "type": "wait",
        "waitTime": 6
      }
    ],
    "index": 0,
    "triggers": []
  }
]

Rest Endpoint Result : {
    "result": {
        "Failed_list": [
            {}
        ],
        "Failed": 0,
        "Successful": 1
    }
}

```

## Common pitfalls:

     1. Check the plugin id in the code with service.yml configuration. Ensure that both the plugin id's are the same.
    
     2. Check the plugin version in the code with service.yml configuration. Ensure that both the versions are the same.
