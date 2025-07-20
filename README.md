
# LibreLoginProd

Fork of the **LibreLogin** (previously LibrePremium) which has caused many problems with newest minecraft versions.
LibreLogin did not meet our expectations, which is why this fork was created.

# Maintenance

Project is currently maintained by the **Sapphire Code sp. z o.o.** company team.

# Quick information

<img src="https://img.shields.io/badge/Java%20version-%2017+-blue?style=for-the-badge&logo=java&logoColor=white"
alt="Plugin requires Java 17 or newer"></img>

<a href="https://discord.gg/WTtEQneRJb">
<img src="https://img.shields.io/badge/Discord-%20SUPPORT-blue?style=for-the-badge&logo=discord&logoColor=white" 
alt="Support available on Discord"></img>
</a>
<a href="https://github.com/Navio1430/LibreLoginProd/wiki">
<img src="https://img.shields.io/badge/Documentation-555555?style=for-the-badge&logo=wikipedia" alt="Documentation on the Wiki"></img>
</a>

<a href="https://github.com/Navio1430/LibreLoginProd/graphs/contributors">
<img src="https://img.shields.io/badge/Contributors-Credits-blue?style=for-the-badge" 
alt="Contributors listed"></img>
</a>

## Basic set of features

- AutoLogin for premium players
- TOTP 2FA (Authy, Google Authenticator...) [details](https://github.com/Navio1430/LibreLoginProd/wiki/2FA)
- Session system
- Name validation (including case sensitivity check)
- Automatic data migration for premium players
- Migration of a player's data by using one command
- Geyser (Bedrock) support using [Floodgate](https://github.com/Navio1430/LibreLoginProd/wiki/Floodgate)

## Platforms
- [✔️] Velocity - up to 1.21.8
- [✔️] Paper - up to 1.21.8
- [❌] BungeeCord - no longer supported, do not use it for production

# FAQ

### What does prod mean?
This means that the project is a heavily modified version intended for production use.

### Why is the plugin almost 36MB?
It's because the libby (gradle plugin) has been removed.
It was responsible for downloading dependencies at runtime.
In the future there will be a similar solution.

# License

Project is licensed under the Mozilla Public License 2.0.
[Read the license here.](https://github.com/Navio1430/LibreLoginProd/blob/master/LICENSE)