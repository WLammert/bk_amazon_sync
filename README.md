# BK_Sync

### Authentication
Authentication is provided via Bearer-Token and needs to be set as environment variable
<br />`BK_TOKEN=xxxxxxxxxxxxxxxx` 

### Docker
#### Create new version:

`./gradlew clean jar`

`docker build -t dev089/bk-sync-app:latest -t dev089/bk-sync-app:0.X.X .`

#### Push to Dockerhub
`docker login`<br />
`docker push dev089/bk-sync-app:0.X.X`<br />
`docker push dev089/bk-sync-app:latest`
