package group.skids.requiem.mixin.loader;

import net.minecraftforge.fml.common.asm.transformers.AccessTransformer;

import java.io.IOException;

public class RequiemAccessTransformer extends AccessTransformer {

    public RequiemAccessTransformer() throws IOException {
        super("requiem_at.cfg");
    }
}
