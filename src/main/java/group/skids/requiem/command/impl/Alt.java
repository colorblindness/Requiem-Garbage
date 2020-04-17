package group.skids.requiem.command.impl;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.realmsclient.gui.ChatFormatting;
import group.skids.requiem.client.Requiem;
import group.skids.requiem.command.Command;
import group.skids.requiem.mixin.accessors.IMinecraft;
import group.skids.requiem.utils.Printer;
import group.skids.requiem.utils.thealtening.TheAltening;
import group.skids.requiem.utils.thealtening.domain.AlteningAlt;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

import java.io.*;
import java.util.Objects;

public class Alt extends Command {

    public Alt() {
        super("Alt",new String[]{"a","alt","alts"});
    }

    @Override
    public void onRun(String[] args) {
        if (args.length > 2) {
            if (args[1].toLowerCase().contains("altening")) {
                Requiem.INSTANCE.switchToTheAltening();
                if (args[2].contains("@")) {
                    run(args[2], "geraldBFigley");
                } else {
                    try {
                        final TheAltening theAltening = new TheAltening(args[2]);
                        AlteningAlt account = theAltening.generateAccount(theAltening.getUser());
                        run(Objects.requireNonNull(account).getToken().replaceAll(" ", ""), "geraldBFigley");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (args.length > 3) {
                Requiem.INSTANCE.switchToMojang();
                run(args[2], args[3]);
            } else Printer.print("Not enough arguments!");
        } else Printer.print("Not enough arguments!");
    }

    private Session createSession(String username, String password) {
        YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(java.net.Proxy.NO_PROXY, "");
        YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(username);
        auth.setPassword(password);
        try {
            auth.logIn();

            return new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
        } catch (AuthenticationException localAuthenticationException) {
            localAuthenticationException.printStackTrace();
        }
        return null;
    }

    public void run(String username, String password) {
        final File altFile = new File(Requiem.INSTANCE.getPath() + File.separator + "alts.txt");
        if (!altFile.exists()) {
            try {
                altFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Printer.print("Username: " + username);
        Printer.print("Password: " + password);
        Printer.print(ChatFormatting.AQUA + "Logging in...");
        Session auth = createSession(username, password);
        if (auth == null) {
            Printer.print(ChatFormatting.RED + "Login failed!");
        } else {
            Printer.print(ChatFormatting.GREEN + "Logged in. (" + auth.getUsername() + ")");
            ((IMinecraft) Minecraft.getMinecraft()).setSession(auth);
            try {
                Writer output = new BufferedWriter(new FileWriter(altFile, true));
                output.append(username).append(" - ").append(password).append(" - ").append(auth.getUsername()).append("\n");
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
