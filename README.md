# Pf4jBulkSaveApiPlugin
Pf4j Bulk Save API Plugin

This bulk save api plugin involves only gate microservice.

     1. Gate

There are 2 ways of deploying this plugin in the spinnaker.

## Method 1

   #### Tasks list

   - [x] Run `./gradlew clean fatJar`.
   - [x] Copy the `batchUpdate-gate-api/build/libs/com.opsmx.gate.bulksave.enabled.plugin.jar` into your spinnaker gate plugin root repository.
   - [x] Configure the Spinnaker gate service. Put the following in the gate-local.yml to enable the plugin and configure the extension.
   
          1.   Gate configuration
Adding the following to your gate.yml or ~/.hal/default/profiles/gate-local.yml config will load and start the latest Bulk Save API plugin during app startup.
```
spinnaker:
  extensibility:
    plugins:
      Opsmx.CustomStagePlugin:
        enabled: true
        version: 1.0.1
        config:
          defaultVmDetails: '{
                              "username": "ubuntu",
                              "password": "xxxxx",
                              "port": 22,
                              "server": "xx.xx.xx.xx"
                            }'
          defaultgitAccount: '{
                                "artifactAccount": "my-github-artifact-account",
                                "reference": "https://api.github.com/repos/opsmx/Pf4jCustomStagePlugin/contents/script.sh",
                                "type": "github/file",
                                "version": "main"
                              }'
    repositories:
      opsmx-repo:
        url: https://raw.githubusercontent.com/opsmx/spinnakerPluginRepository/master/repositories.json
```

## Method 2

   To be added.

## Check the logs to confirm the plugin started successfully:

   #### Gate logs
```
2021-03-12 07:43:02.827  INFO 15527 --- [           main] org.pf4j.AbstractPluginManager           : [] No plugins
2021-03-12 07:43:04.472  INFO 15527 --- [           main] org.pf4j.util.FileUtils                  : [] Expanded plugin zip 'Opsmx.CustomStagePlugin-pf4jCustomStagePlugin-v1.0.1.zip' in 'Opsmx.CustomStagePlugin-pf4jCustomStagePlugin-v1.0.1'
2021-03-12 07:43:04.490  INFO 15527 --- [           main] org.pf4j.util.FileUtils                  : [] Expanded plugin zip 'orca.zip' in 'orca'
2021-03-12 07:43:04.508  INFO 15527 --- [           main] org.pf4j.AbstractPluginManager           : [] Plugin 'Opsmx.CustomStagePlugin@1.0.2' resolved
2021-03-12 07:43:04.509  INFO 15527 --- [           main] org.pf4j.AbstractPluginManager           : [] Start plugin 'Opsmx.CustomStagePlugin@1.0.2'
2021-03-12 07:43:04.517  INFO 15527 --- [           main] c.o.p.stage.custom.CustomStagePlugin     : [] CustomStagePlugin.start()
```

## Common pitfalls:

     1. Check the plugin id in the code with service.yml configuration. Ensure that both the plugin id's are the same.
    
     2. Check the plugin version in the code with service.yml configuration. Ensure that both the versions are the same.
