package net.cassite.vproxy.app;

import net.cassite.vproxy.component.app.Socks5Server;
import net.cassite.vproxy.component.elgroup.EventLoopGroup;
import net.cassite.vproxy.component.exception.AlreadyExistException;
import net.cassite.vproxy.component.exception.ClosedException;
import net.cassite.vproxy.component.exception.NotFoundException;
import net.cassite.vproxy.component.secure.SecurityGroup;
import net.cassite.vproxy.component.svrgroup.ServerGroups;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Socks5ServerHolder {
    private final Map<String, Socks5Server> map = new HashMap<>();

    public List<String> names() {
        return new ArrayList<>(map.keySet());
    }

    public Socks5Server add(String alias,
                    EventLoopGroup acceptorEventLoopGroup,
                    EventLoopGroup workerEventLoopGroup,
                    InetSocketAddress bindAddress,
                    ServerGroups backends,
                    int timeout,
                    int inBufferSize,
                    int outBufferSize,
                    SecurityGroup securityGroup) throws AlreadyExistException, IOException, ClosedException {
        if (map.containsKey(alias))
            throw new AlreadyExistException();
        Socks5Server socks5Server = new Socks5Server(alias, acceptorEventLoopGroup, workerEventLoopGroup, bindAddress, backends, timeout, inBufferSize, outBufferSize, securityGroup);
        map.put(alias, socks5Server);
        socks5Server.start();
        return socks5Server;
    }

    public Socks5Server get(String alias) throws NotFoundException {
        Socks5Server socks5Server = map.get(alias);
        if (socks5Server == null)
            throw new NotFoundException();
        return socks5Server;
    }

    public void removeAndStop(String alias) throws NotFoundException {
        Socks5Server socks5Server = map.remove(alias);
        if (socks5Server == null)
            throw new NotFoundException();
        socks5Server.destroy();
    }

    void clear() {
        map.clear();
    }

    void put(String alias, Socks5Server socks5) {
        map.put(alias, socks5);
    }
}
