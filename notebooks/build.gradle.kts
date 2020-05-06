plugins {
	base
}

tasks {
	val install = register<Exec>("pipenvInstall") {
		group = "build"
		commandLine("pipenv", "install")
	}

	register<Exec>("startNotebooks") {
		group = "jupyter"
		commandLine("pipenv", "run", "jupyter", "notebook")
		dependsOn(install)
	}
}