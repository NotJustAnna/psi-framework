package net.notjustanna.ktbot

import net.notjustanna.psi.commands.Category;
import net.notjustanna.psi.commands.Command;

@Command("debug")
@Category("kt#debug")
class DebugCommand(override val category:ICategory,override val kodein:Kodein):ICommand,KodeinAware{
    override fun CommandContext.call(){
    val registry:CommandRegistry by instance()

    sendEmbed{
    field("commands",registry.commandNames().toString())
    field("categories",registry.categoryNames().toString())
    }
    }
    }

