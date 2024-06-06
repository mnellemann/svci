# Running the SVCi as Container Podman POD stack 

Start SVCI, InfluxDB and Grafana as container with podman pod:


## Create a pod with network between the containers and export ports for grafana and influxdb.
> :bulb: Traffic out from container is normally allowed.

```shell
podman pod create -n virtualize_metric_stack -p 8086:8086 -p 3000:3000
podman run --name influxdb -d -p 8086:8086 --pod virtualize_metric_stack influxdb:latest
podman run --name grafana -d --pod virtualize_metric_stack grafana/grafana
```

## Starting the SVCi - Virtualize Metric Container.
Create a folder that contains the svi.toml
Crate the svci.toml file with with your credentials/token for influxdb and flashsystem, se example below.

```shell
# Run interactive to check.
podman run -i --name virtualize_exporter --pod virtualize_metric_stack  --volume ${PWD}/data:/opt/app/config/ ghcr.io/mnellemann/svci:main
```
See that you get output `"[main] InfluxClient"`

## Run container in background
```shell
# Run in background
podman run --name virtualize_exporter -d --pod virtualize_metric_stack  --volume ${PWD}/data:/opt/app/config/ ghcr.io/mnellemann/svci:main
```


<details closed>
  <summary>:bulb: Run container with options</summary>
    
    podman run --name virtualize_exporter -pod virtualize_metric_stack --volume ${PWD}/data:/opt/app/config/ ghcr.io/mnellemann/svci:main java -jar /opt/app/svci.jar/svci-latest.jar -c /opt/app/config/svci.toml -d
    
</details>



## Check status of the Virtualize_Metric POD:

Check that all containers is running with `podman ps --pods`

```shell
podman ps --pod                        

CONTAINER ID  IMAGE                                                  COMMAND               CREATED       STATUS             PORTS                                           NAMES                POD ID        PODNAME
2c78533009c1  localhost/podman-pause:5.0.0-dev-8a643c243-1710720000                        2 months ago  Up About a minute  0.0.0.0:3000->3000/tcp, 0.0.0.0:8086->8086/tcp  19b5b49164c9-infra   19b5b49164c9  virtualize_metric_stack
2e84bf1ee381  docker.io/library/influxdb:latest                      influxd               2 months ago  Up About a minute  0.0.0.0:3000->3000/tcp, 0.0.0.0:8086->8086/tcp  influxdb             19b5b49164c9  virtualize_metric_stack
cd8844645b21  docker.io/grafana/grafana:latest                                             2 months ago  Up About a minute  0.0.0.0:3000->3000/tcp, 0.0.0.0:8086->8086/tcp  grafana              19b5b49164c9  virtualize_metric_stack
06cdd8cbb9b8  ghcr.io/mnellemann/svci:main                           java -jar /opt/ap...  28 hours ago  Up About a minute  0.0.0.0:3000->3000/tcp, 0.0.0.0:8086->8086/tcp  virtualize_exporter  19b5b49164c9  virtualize_metric_stack
```

To get logs from the virtualize_exporter you can use podman logs
```shell
podman logs -f virtualize_exporter
```

To log into the container use: exec -it with /bin/sh
```shell
podman exec -it virtualize_exporter /bin/sh
```

## Example Create the config file for Virtualize_Metric Container.

We are providing a config file to supply the SVCi/Virtualize Metric with an IP and a user. This will enable it to connect to both the Flashsystem/SVC and InfluxDB.



Create a Main folder to store the config file.
in the example we are using mainfolder virtualize-metric and data. 
place the config file, svci.toml inside data folder.

```shell
% pwd                     
-/virtualize-metric
% ls
data
% ls         
svci.toml
```
> :bulb: You could also created a container volume and add the config file.



## Example Config Virtualize_Metric Container

Example for influxDB and one storage system.

> :bulb: To monitor more then one storage system just duplicated the [svc.xx] part. 

```shell
# SVCi Configuration

# InfluxDB to save metrics
[influx]
url = "http://localhost:8086"
# for InfluxDB V1 use Username and Password
#username = "admin"
#password = "adminadmin24"
bucket = "svci"
# for InfluxDB V2 use token
token = "hiD739k61IhU0Z2zrqQYMR6TCk1Tj3yhgmXZt9-dlNyzxxYyyKC1XXqc1InLYWnQZyaK6tIfj7ATT_feREPMGA=="

## Notice:  use hostname without Http and port, default to 7443 and 22 for scp
[svc.fs5200]
hostname = "10.10.10.50"
username = "monitoruser"
password = "SuperDuperPassword!"
refresh = 30
trust = true   # Ignore SSL cert. errors

###
### Define one or more SVC's to query for metrics
### Each entry must be named [svc.<something-unique>]
###


```

