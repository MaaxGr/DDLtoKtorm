package de.maaxgr.ddltoktorm.backend.userinterface

import de.maaxgr.ddltoktorm.backend.userinterface.routes.postGenerate
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class Webserver {

    fun run() {

        embeddedServer(Netty, port = 8070) {

            install(ContentNegotiation) {
                gson {  }
            }

            routing {
                get("/") {
                    call.respond("Hello DDLToKtorm!")
                }

                postGenerate()

            }

        }.start(true)

    }

}