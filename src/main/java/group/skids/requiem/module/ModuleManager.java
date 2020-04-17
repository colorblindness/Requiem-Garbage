package group.skids.requiem.module;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.KeyPressedEvent;
import group.skids.requiem.module.impl.combat.*;
import group.skids.requiem.module.impl.exploits.*;
import group.skids.requiem.module.impl.movement.*;
import group.skids.requiem.module.impl.other.*;
import group.skids.requiem.module.impl.player.*;
import group.skids.requiem.module.impl.visuals.*;
import group.skids.requiem.utils.value.Value;
import net.b0at.api.event.Subscribe;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModuleManager {
    private final Map<String, Module> map = new HashMap<>();
    private File dir;

    public void initializeModules() {
        registerMod(HUD.class);
        registerMod(Sprint.class);
        registerMod(Step.class);
        registerMod(ESP.class);
        registerMod(AntiHunger.class);
        registerMod(KillAura.class);
        registerMod(NoVelocity.class);
        registerMod(Speed.class);
        registerMod(Phase.class);
        registerMod(NoRotate.class);
        registerMod(Sneak.class);
        registerMod(SpeedMine.class);
        registerMod(AutoMine.class);
        registerMod(Notifications.class);
        registerMod(InvWalk.class);
        registerMod(Jesus.class);
        registerMod(Freecam.class);
        registerMod(NoPush.class);
        Requiem.INSTANCE.getBus().registerListener(this);
        loadModules();
    }

    @Subscribe
    public void onKeyPressed(KeyPressedEvent event) {
        for (Module module : map.values()) {
            if (module.getKeybind() == event.getKey()) module.setEnabled(!module.isEnabled());
        }
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public Map<String, Module> getModuleMap() {
        return map;
    }

    public boolean isModule(final String modulename) {
        for (Module mod : getModuleMap().values()) {
            if (mod.getLabel().equalsIgnoreCase(modulename)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Module> getModulesInCategory(Module.Category category) {
        final ArrayList<Module> mods = new ArrayList<>();
        for (Module module : map.values()) {
            if (module.getCategory() == category) {
                mods.add(module);
            }
        }
        return mods;
    }
    public Module getModule(String name) {
        return getModuleMap().get(name.toLowerCase());
    }


    private void registerMod(Class<? extends Module> moduleClass) {
        try {
            final Module createdModule = moduleClass.newInstance();
            for (final Field field : createdModule.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    final Object obj = field.get(createdModule);

                    if (obj instanceof Value)
                        createdModule.getValues().add((Value) obj);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            map.put(createdModule.getLabel().toLowerCase(), createdModule);
        } catch (Exception ignored) {
        }
    }

    public void saveModules() {
        File[] files = dir.listFiles();
        if (!dir.exists()) {
            dir.mkdir();
        } else if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
        getModuleMap().values().forEach(module -> {
            File file = new File(dir, module.getLabel() + ".json");
            JsonObject node = new JsonObject();
            module.save(node, true);
            if (node.entrySet().isEmpty()) {
                return;
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                return;
            }
            try (Writer writer = new FileWriter(file)) {
                writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(node));
            } catch (IOException e) {
                file.delete();
            }
        });
        files = dir.listFiles();
        if (files == null || files.length == 0) {
            dir.delete();
        }
    }

    public void loadModules() {
        if (!dir.exists()) dir.mkdir();
        getModuleMap().values().forEach(module -> {
            final File file = new File(dir, module.getLabel() + ".json");
            if (!file.exists()) {
                return;
            }
            try (Reader reader = new FileReader(file)) {
                JsonElement node = new JsonParser().parse(reader);
                if (!node.isJsonObject()) {
                    return;
                }
                module.load(node.getAsJsonObject());
            } catch (IOException e) {
            }
        });
    }
}
