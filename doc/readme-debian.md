# Instructions for Debian / Ubuntu Systems

Please note that the software versions referenced in this document might have changed and might not be available/working unless updated.

All commands should be run as root or through sudo.

## Install the Java Runtime from repository

```shell
apt-get install default-jre-headless wget
```


## Download and Install InfluxDB

```shell
wget https://dl.influxdata.com/influxdb/releases/influxdb_1.8.10_amd64.deb
dpkg -i influxdb_1.8.10_amd64.deb
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
apt-get install -y adduser libfontconfig1
wget https://dl.grafana.com/oss/release/grafana_9.1.7_amd64.deb
dpkg -i grafana_9.1.7_amd64.deb
systemctl daemon-reload
systemctl enable grafana-server
systemctl start grafana-server
```

When logged in to Grafana (port 3000, admin/admin) create a datasource that points to the local InfluxDB. Now import the provided dashboards.


## Download and Install svci

[Download](https://git.data.coop/nellemann/-/packages/generic/svci/) the latest version of SVCi packaged for deb.

```shell
wget https://git.data.coop/api/packages/nellemann/generic/svci/v0.0.3/svci_0.0.3-1_all.deb
dpkg -i svci_0.0.3-1_all.deb
cp /opt/svci/doc/svci.toml /etc/
cp /opt/svci/doc/svci.service /etc/systemd/system/
systemctl daemon-reload
systemctl enable svci
```

Now modify */etc/svci.toml* and test setup by running ```/opt/svci/bin/svci -d``` manually and verify connection to SVC and InfluxDB. Afterwards start service with ```systemctl start svci``` .
