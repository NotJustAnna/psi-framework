package net.notjustanna.javabot;

import com.mewna.catnip.entity.builder.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import net.notjustanna.psi.commands.Category;
import net.notjustanna.psi.commands.Command;
import net.notjustanna.psi.commands.ICategory;
import net.notjustanna.psi.commands.context.CommandContext;

import static net.notjustanna.psi.exported.PsiExported.psi_version;

@Command({"about", "thanks"})
@Category("j#info")
class AboutCommand extends AbstractCommand {
    public AboutCommand(ICategory c) {
        category = c;
    }

    @Override
    public void call(@NotNull CommandContext ctx) {
        ctx.getChannel().sendMessage(
            new EmbedBuilder()
                .description("This is an example Kotlin bot made using psi " + psi_version + ".\nThanks for using it <3.")
                .build()
        );
    }
}
