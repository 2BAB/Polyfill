import me.xx2bab.polyfill.Polyfill
import org.gradle.api.Project
import java.io.File

object FunctionTestFixtures {

    @Volatile
    private var polyfill: Polyfill? = null

    fun getPolyfill(project: Project): Polyfill {
        return polyfill ?: synchronized(Polyfill::class.java) {
            polyfill ?: Polyfill(project).also{
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