package by.aermakova.fiftyusersloader.data.model

interface Mapper<Local, Remote> {
//    fun Local.toRemote(): Remote
    fun Remote.toLocal(): Local
//    fun List<Local>.toRemote(): List<Remote> = this.map { it.toRemote() }
    fun List<Remote>.toLocal(): List<Local> = this.map { it.toLocal() }
}
