# Register APISIX itself in Consul.
# If the Consul agent cannot resolve `apisix-gateway`, replace it with the
# actual container DNS name or reachable IP of the gateway.
service {
  name = "apisix-gateway"
  id   = "apisix-gateway"

  address = "apisix-gateway"
  port    = 9080
  tags    = ["gateway", "apisix", "http"]

  meta = {
    admin_port = "9180"
  }

  check {
    id       = "apisix-gateway-tcp"
    name     = "APISIX Gateway TCP 9080"
    tcp      = "apisix-gateway:9080"
    interval = "10s"
    timeout  = "2s"
  }
}
