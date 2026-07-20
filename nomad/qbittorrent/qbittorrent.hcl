job "qbittorrent" {
  datacenters = ["dc1"]
  type        = "service"

  group "download" {
    count = 1
    restart {
      attempts = 3
      delay    = "15s"
      mode     = "delay"
    }
    network {
      mode = "bridge"
      port "webui" {
        static = 8080
        to     = 8080
      }
      port "torrent" {
        static = 6881
        to     = 6881
      }
    }
    task "qbittorrent" {
      driver = "docker"
      config {
        image = "lscr.io/linuxserver/qbittorrent:latest"
        ports = [
          "webui",
          "torrent"
        ]
      }
      env {
        PUID       = "1000"
        PGID       = "1000"
        TZ         = "Etc/UTC"
        WEBUI_PORT = "8080"
      }
      volume_mount {
        volume      = "qbittorrent-config"
        destination = "/config"
        read_only   = false
      }
      volume_mount {
        volume      = "nfs-downloads"
        destination = "/downloads"
        read_only   = false
      }
      resources {
        cpu    = 500
        memory = 512
      }
    }
    volume "qbittorrent-config" {
      type      = "host"
      read_only = false
      source    = "qbittorrent-config-host" 
    }
    volume "nfs-downloads" {
      type      = "host"
      read_only = false
      source    = "nfs-downloads-host" 
    }
  }
}
