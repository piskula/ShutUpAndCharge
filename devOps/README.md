# Running Application on GCP

## Prepare domain
1. Go to cloudflare and register new domain
2. Go to `profile` -> `api tokens` and create API token `Zone:DNS:Edit`
3. Store the token on safe place

## Initiate VM
1. Prepare ssh key (e.g. with PuttyGen)
2. Login to GCP console and create new VM instance
   1. Go to SSH keys and add your key
   2. Create Instance Template (Ubuntu)
      - europe-west1
      - Firewall
      - Management -> Startup script `sudo ufw allow 22`
   3. Create Instance Group from the template (this will create an instance)
   4. Go to Network - default and verify there is Firewall ~~http/~~ https
      - if not, create it manually for ~~http:80 and~~ https:443
3. Copy instance external IP and connect via ssh (Putty) and key pair
4. In CloudFlare, create new DNS rule to point to your external IP
   - keycloak, proxy status DNS only
5. Perform updates and install necessary tools
   - `sudo apt-get update`
   - `sudo apt-get upgrade`
   - ~~`sudo apt-get install docker.io docker-compose`~~
   - install `docker compose`, via e.g.:
       ```bash
       DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
       mkdir -p $DOCKER_CONFIG/cli-plugins
       curl -SL https://github.com/docker/compose/releases/download/v2.12.2/docker-compose-linux-x86_64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
       ```
6. Add your user to docker group
   - `sudo usermod -aG docker $USER`
   - `sudo chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose`
7. Install Java
  - ~~`sudo apt install default-jre`~~
  - `sudo apt install openjdk-21-jdk`
8. Git clone [keycloak-production](https://github.com/piskula/keycloak-production) repo into the machine and follow instructions there
   - After this step, you will have secured instance (HTTPS) available on your domain with:
     - shared PostgresDB (used for keycloak and charging app itself)
     - keycloak
     - nginx (routing traffic from default HTTP/S port to different components)
