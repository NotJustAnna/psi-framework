package net.notjustanna.ktbot

import net.notjustanna.psi.commands.Category
import net.notjustanna.psi.commands.ICategory

@Category("kt")
enum class KtCategories(override val categoryName: String, override val nsfw: Boolean = false) : ICategory {
    INFO("Information"),
    DEBUG("Debug Commands")
}