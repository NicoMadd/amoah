# Amoah

## Summary

**Amoah** is a process controller which lets you instanciate in forked processes different jars, pass variables to them, and control their status.

It is still pretty raw so its WIP. The idea is to handle different processes similar to a Docker or Kubernetes. Let's say its a raw orchestrator.

## Features

### With Configuration :wrench:

With a simple configuration you can customize the execution.

### With Dockerfile :whale2:

The idea (WIP) is to handle your own Dockerfile and **Amoah** should do the rest and handle the instance.

## Documentation

### Config.json

```

{
    "services": {
        "serviceA":{
            "path": "/services/service.jar",
            "port": 8080,
            "logDir": "/var/log/",
            "env": {
                "A":"valueA",
                "B":"valueB
            }
        },
        "serviceB":{
            "path": "/services/service.jar",
            "port": 8080,
            "logDir": "/var/log/",
            "env": {
                "A":"valueA",
                "B":"valueB
            }
        }
    }
}

```

-   _path_: Indicates the main executable file.
-   _port_: The port to be opened for external connections.
-   _logDir_: The log directory to write for the Service Controller. (Note: this is not the log for the main executable file)
-   _env_: The environment variables to pass to the Service Controller and to the forked process.
