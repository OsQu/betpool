import io.restassured.RestAssured.get
import org.amshove.kluent.shouldEqual
import io.restassured.RestAssured.given
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

class AppSpec : Spek({
    jooby(App()) {
        describe("GET /") {
            given("query parameter name=Osku") {
                it("returns Hello Osku!") {
                    given().queryParam("name", "Osku")
                        .`when`()
                        .get("/test")
                        .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .asString()
                        .let {
                            it shouldEqual "Hello Osku!"
                        }

                }
            }

            given("no parameters") {
                it("returns Hello Kotlin") {
                    get("/test").then().assertThat()
                        .statusCode(200)
                        .extract()
                        .asString()
                        .let {
                            it shouldEqual "Hello Kotlin!"
                        }
                }
            }
        }
    }
})