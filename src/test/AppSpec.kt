import io.restassured.RestAssured.get
import org.amshove.kluent.shouldEqual

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class AppSpec : Spek({
    jooby(App()) {
        describe("GET /") {
            given("no parameters") {
                it("returns Hello Kotlin") {
                    get("/state").then().assertThat()
                        .statusCode(200)
                        .extract()
                        .asString()
                        .let {
                            it shouldEqual "{}"
                        }
                }
            }
        }
    }
})