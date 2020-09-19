import me.xx2bab.polyfill.ApplicationPolyfill
import me.xx2bab.polyfill.Polyfill
import org.gradle.api.Project
import java.io.File

object FunctionTestFixtures {

    @Volatile
    private var polyfill: ApplicationPolyfill? = null

    fun getApplicationPolyfill(project: Project): ApplicationPolyfill {
        return polyfill ?: synchronized(ApplicationPolyfill::class.java) {
            polyfill ?: Polyfill.createApplicationPolyfill(project).also{
                polyfill = it
            }
        }
    }

    fun getOutputFile(project: Project,
                      fileName: String): File {
        return File(project.rootProject.buildDir,
                "functionTestOutput" + File.separator + fileName).apply {
            createNewFile()
        }
    }

}