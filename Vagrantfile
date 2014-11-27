# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "ubuntu/trusty64"
  config.vm.network :forwarded_port, guest: 8080, host: 8080, host_ip: "127.0.0.1", auto_correct: true # Solr
  config.vm.network :forwarded_port, guest: 9092, host: 9092, host_ip: "127.0.0.1", auto_correct: true # Kafka
  config.vm.network :forwarded_port, guest: 2181, host: 2181, host_ip: "127.0.0.1", auto_correct: true # Zookeeper
  config.vm.provision :shell, path: "vagrant/setup.sh"
end
