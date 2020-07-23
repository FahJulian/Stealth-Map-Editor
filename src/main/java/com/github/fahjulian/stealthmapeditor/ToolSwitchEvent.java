package com.github.fahjulian.stealthmapeditor;

import com.github.fahjulian.stealth.core.event.AbstractEvent;

public class ToolSwitchEvent extends AbstractEvent
{
    private final Tool tool;

    public ToolSwitchEvent(Tool tool)
    {
        this.tool = tool;
        dispatch();
    }

    public Tool getTool()
    {
        return tool;
    }
}
