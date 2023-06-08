
package net.mcreator.element.converter.v2023_2;

import com.google.gson.JsonElement;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.converter.IConverter;
import net.mcreator.element.types.LivingEntity;
import net.mcreator.io.FileIO;
import net.mcreator.workspace.Workspace;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
public class AttackOnCollideFixer implements IConverter {
  
	@Override public int getVersionConvertingTo() {
		return 43;
	}
}
