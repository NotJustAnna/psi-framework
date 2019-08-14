package net.notjustanna.utils.extensions.discordapp

import net.notjustanna.utils.extensions.lang.replaceEach

fun String.safeUserInput() = replaceEach(
    "@everyone" to "@\u200Beveryone",
    "@here" to "@\u200Bhere"
)