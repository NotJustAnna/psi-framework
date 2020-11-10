package net.notjustanna.javabot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.notjustanna.psi.commands.ICategory;
import net.notjustanna.psi.commands.ICommand;
import net.notjustanna.psi.commands.context.CommandContext;
import net.notjustanna.psi.commands.help.HelpProvider;

public abstract class AbstractCommand implements ICommand {
    protected ICategory category;
    protected boolean nsfw;

    protected AbstractCommand() {
    }

    @Nullable
    @Override
    public ICategory getCategory() {
        return category;
    }

    @Override
    public boolean getNsfw() {
        return nsfw;
    }

    @Nullable
    @Override
    public HelpProvider getHelp() {
        return null;
    }

    @Override
    public abstract void call(@NotNull CommandContext ctx);
}
