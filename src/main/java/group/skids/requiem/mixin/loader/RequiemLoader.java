package group.skids.requiem.mixin.loader;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.tools.obfuscation.mcp.ObfuscationServiceMCP;

import java.util.List;
import java.util.Map;


@IFMLLoadingPlugin.Name("Requiem")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class RequiemLoader implements IFMLLoadingPlugin {

    public RequiemLoader() {
        MixinBootstrap.init();

        String obfuscation = ObfuscationServiceMCP.NOTCH;
        for (String s : (List<String>) Launch.blackboard.get("TweakClasses")) {
            if (s.contains("net.minecraftforge.fml.common.launcher")) {
                obfuscation = ObfuscationServiceMCP.SEARGE;
                break;
            }
        }

        MixinEnvironment.getDefaultEnvironment().setSide(MixinEnvironment.Side.CLIENT);
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext(obfuscation);

        Mixins.addConfiguration("mixins.requiem.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return RequiemAccessTransformer.class.getName();
    }

}
