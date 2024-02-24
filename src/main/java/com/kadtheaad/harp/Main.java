package com.kadtheaad.harp;

import com.kadtheaad.harp.core.Networker;
import com.kadtheaad.harp.core.HARP;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            GUI.main();
            return;
        }
        ArgumentParser parser = ArgumentParsers.newFor("HARP").build()
                .defaultHelp(true)
                .description("HARP is a library for locating and verifying devices on a network securely.")
                .version("1.0.0");
        parser.addArgument("-s", "--sender")
                .action(Arguments.storeTrue())
                .help("Run as a sender");
        try {
            List<String> networkInterfaces = new ArrayList<>();
            Networker.getInterfaces().forEach(networkInterface -> networkInterfaces.add(networkInterface.getName()));
            parser.addArgument("-i", "--interface")
                    .choices(networkInterfaces)
                    .setDefault(networkInterfaces.get(0))
                    .help("The network interface to use");
        } catch (SocketException ignored) {}

        parser.addArgument("-t", "--to")
                .help("The address of the other computer you are trying to connect to")
                .required(true);
        parser.addArgument("-f", "--from")
                .help("Your address")
                .required(true);
        parser.addArgument("-p", "--password")
                .help("The password that is used to verify the other computer and is used to create a secure connection. Must be the same on both computers.")
                .required(true);

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        List<InetAddress> broadcastAddresses = Networker.getBroadcastAddresses(ns.getString("interface"));

        if (ns.getBoolean("sender"))
            new HARP(true, ns.getString("to").getBytes(), ns.getString("from").getBytes(), ns.getString("password"), broadcastAddresses.get(0)).run();
        else
            new HARP(false, ns.getString("to").getBytes(), ns.getString("from").getBytes(), ns.getString("password"), broadcastAddresses.get(0)).run();
    }
}
