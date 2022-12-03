package de.maaxgr.ddltoktorm.backend.userinterface

import de.maaxgr.ddltoktorm.backend.userinterface.routes.postGenerate
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

class Webserver {

    fun run() {

        embeddedServer(Netty, port = 8070) {

            install(ContentNegotiation) {
                gson { }
            }

            install(CORS) {
                method(HttpMethod.Options)
                method(HttpMethod.Put)
                method(HttpMethod.Delete)
                method(HttpMethod.Patch)
                header(HttpHeaders.Authorization)
                anyHost()
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