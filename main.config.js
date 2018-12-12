var worker = {
      name      : 'ServerWS',
      script    : 'java',
      cwd       : "C:/Users/sarthak_z9/Desktop/minor2A",
      exec_interpreter: "none",
      exec_mode : "fork",
      instances : 1,
      args: [
                "-Xmx50m",
                "-cp",
                "BBB.jar",
                "com.asambhava.groupchatws.ServerWS"
           ],

      env: {
            PORT : 9000
      },
      env_production : {
         NODE_ENV: 'production'
     },
     max_restarts : 10,
}
var Allapps=[];
for(var i=0;i<=3;i++)
{
     Allapps[i] = JSON.parse(JSON.stringify(worker));
     Allapps[i].name = worker.name+'_'+i;
     Allapps[i].env.PORT = parseInt(worker.env.PORT)+i;
}
console.log(Allapps);
module.exports = {
  /**
   * Application configuration section
   * http://pm2.keymetrics.io/docs/usage/application-declaration/
   */
  apps : Allapps
};
