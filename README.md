#Githubrank Challenge

###In order to use: 

- firstly be so kind and checkout the code :)
- use cmd line client and go to the main project folder `~/.../gitrank`
- run the following command `sbt buildAndRun` . Of course if you have `sbt` already installed
- when you will see `[info] p.c.s.AkkaHttpServer - Listening for HTTP on /0:0:0:0:0:0:0:0:8080` in your command line,
  you are good to go to open your favourite browser and hit `localhost:8080` 
- you will find there a brief "hello" with hint about what endpoint to invoke. For the impatient, it will be 
  `http://localhost:8080/org/{org_name}/contributors`
    
- change `{org_name}` to a real organization name and check how and how many contributions did.
You can check e.g. `http://localhost:8080/org/engineyard/contributors`

##HINT
Remember to set `GH_TOKEN` env variable with you Personal Access Token generated from Github, in order to exceed
the rate limit and get a nasty 403 and being not able to check the contributors.