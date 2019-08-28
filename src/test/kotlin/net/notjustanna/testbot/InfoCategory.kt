package net.notjustanna.testbot

import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.ICategory

@Category("info")
class InfoCategory : ICategory {
    override val categoryName = "Information"
    override val nsfw = false
}