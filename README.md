# Server Manager

### What's this?

Welcome to The Server Manager project!
The goal of Server Manager is to allow users to easily control (and delegate control) of servers within
[Pterodactyl](https://pterodactyl.io/) via Discord.

_Note: Pterodactyl is commonly used to run game servers, but can technically run any Docker container - 
as long as you can add the "server" to Pterodactyl, it is supported by Server Manager._

### How it works

Server Manager currently has two concepts of authorization, done via roles in Discord. These are defined as a "Moderator"
and an "Administrator".

Moderators can "invoke" the same actions as an Administrator, however whenever a Moderator invokes an Action, 
if it is a "disruptive" operation, it will need multiple Moderators to confirm the invocation. Administrators however
can override this, and instantly confirm an invocation.

When an Administrator invokes an Action, it is confirmed, and runs, immediately.

### What is an "Invocation" / invoking an Action?

Taking a step back, an Action in Server Manager is a pending operation on a server. Actions will stay in a pending state
until it is either confirmed, or times out (after 20 seconds).

Actions are "invoked" by a user, as such, an invocation is simply a user requesting an Action. 

![](https://i.russ.network/-lZc_M7o.png)
![](https://i.russ.network/LfHr1i3J.png)
![](https://i.russ.network/SBHRZiE6.png)

### Fantastic! Where can I get it?

There are two ways to obtain Server Manager at the moment:

- Each commit (whether on the main branch or another) triggers a CI build on my [GitLab instance](https://git.russ.network/russjr08/server-manager/-/jobs) and look for the `main` branch's latest `create-jar` build, and select the download icon - this will download a zip file that contains the JAR in it. Note, you'll need Java 16 or higher to run it! 
- You can use the docker image, found currently on my own Docker registry `docker-registry.omnicron.dev/server-manager:latest`

_Note: Server Manager will probably be published on DockerHub with named releases once it's further in development._

### Configuring Server Manager

Before running Server Manager, you should create a folder in the same directory you have the jar, named `data`,
and within that folder create a file called `.env`, you'll need the following contents:

```
BOT_TOKEN=Your Bot's Token from the Discord Developer Portal
PTERODACTYL_URL=https://panel.example.com
PTERODACTYL_API_KEY=API_KEY

DISCORD_MC_MOD_ID=MOD_ROLE_ID_IN_DISCORD
DISCORD_MC_ADMIN_ID=ADMIN_ROLE_ID_IN_DISCORD

# Optional settings, values defined here are default
REQUIRED_MODERATORS_FOR_RESTART=2
REQUIRED_MODERATORS_FOR_STOP=3
```

Here's a breakdown of those variables:

`BOT_TOKEN` - You will need to register an application on the
[Discord Developer Portal](https://discord.com/developers/applications) and create a "Bot" via it,
then Discord will give you a "Bot Token".

`PTERODACTYL_URL` - This needs to be the base URL of where your Pterodactyl instance can be reached.

_Note: If your panel is protected by Cloudflare or anything that occasionally requires a captcha to be entered,
this may break. You'll need to look into whitelisting the API URL or whitelisting the machine where you run Server Manager on._

`PTERODACTYL_API_KEY` - To generate an API key, head over to https://panel.yourdomain.com/account/api - if you use the
IP whitelist, please don't forget to whitelist the IP from which you're running Server Manager.

`DISCORD_MC_MOD_ID` / `DISCORD_MC_ADMIN_ID` - These will be the IDs of the Roles you create in Discord for
Administrators and Moderators. To get these, enable developer mode in Discord's settings, then go to
`Server Settings -> Roles` and right-click the respective role, and click `Copy ID`.

`REQUIRED_MODERATORS_FOR_RESTART` - The amount of moderators required to confirm a restart action.

`REQUIRED_MODERATORS_FOR_STOP` - The amount of moderators required to confirm a stop action.

Server Manager should then list all the servers you have access to at startup 
(note that due to the Pterodactyl API it will only pick up servers that are _owned_ by you).

### Questions or Comments?

Feel free to reach out to me on Discord for any questions you might have, either via DM `@russjr08#5539`
or you can join my Discord via [this link](https://discord.gg/ctzU9mYg6a) in the #development-feedback channel.

If use Matrix, you can also reach out to me on Matrix by DM'ing `@russ:russ.network` as well.