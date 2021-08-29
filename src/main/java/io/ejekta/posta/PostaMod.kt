package io.ejekta.posta

import io.ejekta.kambrik.Kambrik
import net.fabricmc.api.ModInitializer

class PostaMod : ModInitializer {

    internal companion object {
        const val ID = "example"
    }

    private val logger = Kambrik.Logging.createLogger(ID)

    override fun onInitialize() {
        logger.info("Kambrik Sample Mod Says Hello!")
    }

}