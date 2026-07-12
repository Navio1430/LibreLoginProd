

# Maintenance update 🚀 ❗

update: Thanks to [vuxeim](https://github.com/vuxeim) and his contribution we can still use LibreLoginProd with the newest minecraft versions.

I'm not going to maintain this plugin anymore, but still I'm open to PR's.
Here's the new plugin repo: https://github.com/Navio1430/NavAuth.

<br><br>
<br><br>
<div display="flex" justify-content="space-between" align="center">
 <h1>LibreLoginProd 🔐 - autologin plugin</h1>
  <p>Fork of the <b>LibreLogin</b> (previously LibrePremium) which has caused many problems with newest minecraft versions.
LibreLogin did not meet our expectations, which is why this fork was created.</p>
</div>
<br>
<br>

# Contributors, thanks to:

- **[vuxeim](https://github.com/vuxeim)** - for support for the newest minecraft versions
- **original LibreLogin creators** - for creating the LibreLogin

# Quick information 📌

<img src="https://img.shields.io/badge/Java%20version-%2017+-blue?style=for-the-badge&logo=java&logoColor=white"
alt="Plugin requires Java 17 or newer"></img>
<a href="https://discord.gg/WTtEQneRJb">
<img src="https://img.shields.io/badge/Discord-%20SUPPORT-purple?style=for-the-badge&logo=discord&logoColor=white" 
alt="Support available on Discord"></img>
</a>
<a href="https://github.com/Navio1430/LibreLoginProd/graphs/contributors">
<img src="https://img.shields.io/badge/Contributors-Credits-blue?style=for-the-badge" 
alt="Contributors listed"></img>
</a>

<a href="https://github.com/Navio1430/LibreLoginProd/wiki">
<img src="https://img.shields.io/badge/Documentation-Docs-orange?style=for-the-badge&logo=wikipedia" alt="Documentation on the Wiki"></img>
</a>

## Basic set of features 🎯

- AutoLogin for premium players
- TOTP 2FA (Authy, Google Authenticator...) [details](https://github.com/Navio1430/LibreLoginProd/wiki/2FA)
- Session system
- Name validation (including case sensitivity check)
- Automatic data migration for premium players
- Migration of a player's data by using one command

## Platforms ⚙️
- [✔️] Velocity - up to 26.2
- [✔️] Paper - up to 26.2
- [❌] BungeeCord - no longer supported, do not use it for production

## Main changes 

- [📚] Support for the newest Minecraft Paper and Velocity versions
- [❌] No more support for BungeeCord (maybe will be brought back in future)
- [❌] Removed compatibility with NanoLimboPlugin (should not be used on prod)
- [❗] FloodGate (Geyser) is no longer actively supported (as of Minecraft 26.1.2 upwards)

# FAQ

### What does prod mean?
This means that the project is a heavily modified version intended for production use.

### Why is the plugin almost 5MB?
We are currently trying to go down to 500KB, but first we need
to divide whole project into submodules.

### Will the folder name change after installation?
No, we use the same folder and config names as original the **LibreLogin**.

# License

Project is licensed under the Mozilla Public License 2.0.
[Read the license here.](https://github.com/Navio1430/LibreLoginProd/blob/master/LICENSE)
