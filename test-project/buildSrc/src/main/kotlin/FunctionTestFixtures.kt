import org.gradle.api.Project
import java.io.File

object FunctionTestFixtures {

    fun getOutputFile(project: Project,
                      fileName: String): File {
        return File(project.rootProject.buildDir,
                "functionTestOutput" + File.separator + fileName).apply {
            createNewFile()
        }
    }

}