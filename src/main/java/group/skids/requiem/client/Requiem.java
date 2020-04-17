package group.skids.requiem.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import group.skids.requiem.command.CommandManager;
import group.skids.requiem.friend.FriendManager;
import group.skids.requiem.macro.MacroManager;
import group.skids.requiem.module.ModuleManager;
import group.skids.requiem.utils.thealtening.AltService;
import group.skids.requiem.utils.value.impl.StringValue;
import net.b0at.api.event.Event;
import net.b0at.api.event.EventManager;

import java.io.File;
import java.nio.file.Path;

public enum Requiem {
    INSTANCE;
    private final StringValue label = new StringValue("Label","Requiem");
    private final String version = "0.1";
    private final EventManager<Event> bus = new EventManager<>(Event.class);
    private final Path path = new File(System.getProperty("user.home"), ChatFormatting.stripFormatting("Requiem") + "-1.12.2").toPath();
    private final ModuleManager moduleManager = new ModuleManager();
    private final CommandManager commandManager = new CommandManager();
    private final FriendManager friendManager = new FriendManager();
    private final MacroManager macroManager = new MacroManager();
    private final AltService altService = new AltService();
    public void setupClient() {
        if (!path.toFile().exists()) path.toFile().mkdir();
        moduleManager.setDir(new File(path.toString(), "modules"));
        moduleManager.initializeModules();
        friendManager.setDir(new File(path.toString(), "friend"));
        friendManager.getFriendSaving().loadFile();
        commandManager.initialize();
        macroManager.init();
        System.out.println("Setup Client");
    }

    public void shutdownClient() {
        moduleManager.saveModules();
        friendManager.getFriendSaving().saveFile();
        macroManager.save();
        System.out.println("Shutdown Client");
    }

    public void switchToMojang() {
        try {
            altService.switchService(AltService.EnumAltService.MOJANG);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    public void switchToTheAltening() {
        try {
            altService.switchService(AltService.EnumAltService.THEALTENING);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
        }
    }

    public EventManager<Event> getBus() {
        return bus;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public StringValue getLabel() {
        return label;
    }

    public String getVersion() {
        return version;
    }

    public Path getPath() {
        return path;
    }

    public MacroManager getMacroManager() {
        return macroManager;
    }
}
