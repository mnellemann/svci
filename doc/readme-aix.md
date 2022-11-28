# Instructions for AIX Systems

Please note that the software versions referenced in this document might have changed and might not be available/working unless updated.

More details are available in the [README.md](../README.md) file.

- Grafana and InfluxDB can be downloaded from the [Power DevOps](https://www.power-devops.com/) website - look under the *Monitor* section.

- Ensure Java (version 8 or later) is installed and available in your PATH.


## Download and Install svci

```shell
wget https://bitbucket.org/mnellemann/svci/downloads/svci-0.0.1-1_all.rpm
rpm -i --ignoreos svci-0.0.1-1_all.rpm
cp /opt/svci/doc/svci.toml /etc/
```

Now modify */etc/svci.toml* and test your setup by running ```/opt/svci/bin/svci -d```

