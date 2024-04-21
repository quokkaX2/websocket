cd /home/ec2-user/app

CURRENT_PID=$(sudo docker container ls -q)
sudo docker stop $CURRENT_PID

sudo docker build -t websocket-docker .
sudo docker run -d -p 8080:8080 websocket-docker
