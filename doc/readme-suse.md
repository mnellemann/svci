# Instruction for SLES / OpenSUSE Systems

Please note that the software versions referenced in this document might have changed and might not be available/working unless updated.

More details are available in the [README.md](../README.md) file. If you are running Linux on Power (ppc64le) you should look for ppc64le packages at the [Power DevOps](https://www.power-devops.com/) website.

All commands should be run as root or through sudo.

## Install the Java Runtime from repository

```shell
zypper install java-11-openjdk-headless wget
```


## Download and Install InfluxDB

```shell
wget https://dl.influxdata.com/influxdb/releases/influxdb-1.8.10.x86_64.rpm
rpm -ivh influxdb-1.8.10.x86_64.rpm
systemctl daemon-reload
systemctl enable influxdb
systemctl start influxdb
```

Run the ```influx``` cli command and create the *svci* database.

```sql
CREATE DATABASE "svci" WITH DURATION 365d REPLICATION 1;
```

## Download and Install Grafana

```shell
wget https://dl.grafana.com/oss/release/grafana-9.1.7-1.x86_64.rpm
rpm -ivh --nodeps grafana-9.1.7-1.x86_64.rpm
systemctl daemon-reload
systemctl enable grafana-server
systemctl start grafana-server
```

When logged in to Grafana (port 3000, admin/admin) create a datasource that points to the local InfluxDB. Now import the provided dashboards.


## Download and Install SVCi

[Download](https://git.data.coop/nellemann/-/packages/generic/svci/) the latest version of SVCi packaged for rpm.

```shell
wget https://git.data.coop/api/packages/nellemann/generic/svci/v0.0.3/svci-0.0.3-1.noarch.rpm
rpm -ivh svci-0.0.3-1_all.rpm
cp /opt/svci/doc/svci.toml /etc/
cp /opt/svci/doc/svci.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable svci
```

Now modify */etc/svci.toml* and test your setup by running ```/opt/svci/bin/svci -d``` manually and verify connection to SVC and InfluxDB. Afterwards start service with ```systemctl start svci``` .
