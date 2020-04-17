package group.skids.requiem.macro;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import group.skids.requiem.client.Requiem;
import group.skids.requiem.events.KeyPressedEvent;
import group.skids.requiem.macro.Macro;
import net.b0at.api.event.Subscribe;
import net.minecraft.client.Minecraft;

public class MacroManager {
    private Map<String, Macro> macros = new HashMap<>();
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private File macroFile;

    public void init() {
        macroFile = new File(Requiem.INSTANCE.getPath() + File.separator + "macros.json");
        Requiem.INSTANCE.getBus().registerListener(this);
        try {
            if (!macroFile.exists()) {
                macroFile.createNewFile();
                save();
                return;
            }
            load();
        } catch (IOException labelored) {
        }
    }

    @Subscribe
    public void onKey(KeyPressedEvent event) {
        if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) return;
        getMacros().values().forEach(macro -> {
            if (macro.getKey() == event.getKey()) {
                Minecraft.getMinecraft().player.sendChatMessage(macro.getText());
            }
        });
    }

    public void save() {
        if (macroFile.exists()) {
            try (PrintWriter writer = new PrintWriter(macroFile)) {
                writer.print(GSON.toJson(getMacros()));
            } catch (Exception ignored) {
            }
        }
    }

    public void load() {
        try (FileReader inFile = new FileReader(macroFile)) {
            setMacros(GSON.fromJson(inFile, new TypeToken<Map<String, Macro>>() {
            }.getType()));
            if (getMacros() == null) setMacros(new HashMap<>());
        } catch (Exception ignored) {
        }
    }

    public void removeMacroByLabel(String label) {
        macros.remove(label.toLowerCase());
    }

    public Macro getMacro(String label) {
        return macros.get(label.toLowerCase());
    }

    public Macro getMacroByKey(int key) {
        for (Macro macro : macros.values()) {
            if (macro.getKey() == key) {
                return macro;
            }
        }
        return null;
    }

    public File getMacroFile() {
        return macroFile;
    }

    public void clearMacros() {
        macros.clear();
    }

    public void addMacro(String label, int key,String text) {
        macros.put(label.toLowerCase(), new Macro(label, key, text));
    }

    public boolean isMacro(String label) {
        return getMacro(label) != null;
    }

    public void setMacros(Map<String, Macro> macros) {
        this.macros = macros;
    }

    public Map<String, Macro> getMacros() {
        return this.macros;
    }
}