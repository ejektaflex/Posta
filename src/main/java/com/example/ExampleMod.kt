package com.example

import io.ejekta.kambrik.Kambrik
import net.fabricmc.api.ModInitializer

class ExampleMod : ModInitializer {

    internal companion object {
        const val ID = "example"
    }

    private val logger = Kambrik.Logging.createLogger(ID)

    override fun onInitialize() {
        logger.info("Kambrik Sample Mod Says Hello!")
    }

}