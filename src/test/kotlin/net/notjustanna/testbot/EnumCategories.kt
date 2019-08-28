package net.notjustanna.testbot

import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.ICategory

@Category("enum")
enum class EnumCategories(override val categoryName: String, override val nsfw: Boolean = false) : ICategory {
    HELPFUL("Helpful Commands"),
    DEBUG("Debug Commands")
}