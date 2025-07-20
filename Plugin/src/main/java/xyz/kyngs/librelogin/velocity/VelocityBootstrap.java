/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package xyz.kyngs.librelogin.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import xyz.kyngs.librelogin.api.LibreLoginPlugin;
import xyz.kyngs.librelogin.api.provider.LibreLoginProvider;

@Plugin(
        id = "libreloginprod",
        name = "LibreLoginProd",
        version = "@version@",
        authors = "sapphirecode",
        dependencies = {
                @Dependency(id = "floodgate", optional = true),
                @Dependency(id = "luckperms", optional = true),
                @Dependency(id = "protocolize", optional = true),
                @Dependency(id = "redisbungee", optional = true),
                // TODO: Remove nano limbo plugin support
                @Dependency(id = "nanolimbovelocity", optional = true)
        }
)
public class VelocityBootstrap implements LibreLoginProvider<Player, RegisteredServer> {

    ProxyServer server;
    private final VelocityLibreLogin libreLogin;

    @Inject
    public VelocityBootstrap(ProxyServer server, Injector injector) {
        this.server = server;

        libreLogin = new VelocityLibreLogin(this);
        injector.injectMembers(libreLogin);
    }

    @Subscribe
    public void onInitialization(ProxyInitializeEvent event) {
        libreLogin.enable();

        server.getEventManager().register(this, new Blockers(libreLogin.getAuthorizationProvider(), libreLogin.getConfiguration(), libreLogin.getMessages()));
        server.getEventManager().register(this, new VelocityListeners(libreLogin));
    }

    @Override
    public LibreLoginPlugin<Player, RegisteredServer> getLibreLogin() {
        return libreLogin;
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        libreLogin.disable();
    }
}
