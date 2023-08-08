package net.mcreator.ui.ide;

import net.mcreator.java.debug.Breakpoint;
import org.fife.ui.rtextarea.GutterIconInfo;

import javax.annotation.Nullable;

public class GutterBreakpointInfo {

	private final GutterIconInfo gutterIconInfo;

	@Nullable private Breakpoint breakpoint;

	public GutterBreakpointInfo(GutterIconInfo gutterIconInfo) {
		this.gutterIconInfo = gutterIconInfo;
	}

	public GutterIconInfo getGutterIconInfo() {
		return gutterIconInfo;
	}

	@Nullable public Breakpoint getBreakpoint() {
		return breakpoint;
	}

	public void setBreakpoint(@Nullable Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}

}
