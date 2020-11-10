package net.notjustanna.javabot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.notjustanna.psi.commands.Category;
import net.notjustanna.psi.commands.ICategory;
import net.notjustanna.psi.commands.help.HelpProvider;

@Category("j")
public enum JavaCategories implements ICategory {
    INFO("Information"),
    DEBUG("Debug Commands");

    private final String categoryName;

    JavaCategories(String categoryName) {
        this.categoryName = categoryName;
    }

    @NotNull
    @Override
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public boolean getNsfw() {
        return false;
    }

    @Nullable
    @Override
    public HelpProvider getHelp() {
        return null;
    }
}